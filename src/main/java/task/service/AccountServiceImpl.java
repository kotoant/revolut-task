package task.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import task.dao.AccountDao;
import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;
import task.manager.AccountManager;
import task.model.Account;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread safe implementation of {@link AccountService}. If several threads want to modify the same account they will be
 * synchronized by lock for that particular account to prevent inconsistent state.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@ThreadSafe
public class AccountServiceImpl implements AccountService {

    private final AccountManager accountManager;
    private final AccountDao accountDao;

    // This cache keep locks per account id. Weak values guarantee that once the lock is unused the corresponding entity
    // will be removed from the cache.
    private final Cache<Long, Lock> locksByAccountId = CacheBuilder.newBuilder()
            .weakValues() // to avoid memory leak
            .build();

    @Inject
    public AccountServiceImpl(AccountManager accountManager, AccountDao accountDao) {
        this.accountManager = accountManager;
        this.accountDao = accountDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account create(@Nullable BigDecimal amount) {
        final Account account = new Account().setAmount(Optional.ofNullable(amount).orElse(BigDecimal.ZERO));
        accountDao.insert(account);
        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccount(long accountId) {
        return Optional.ofNullable(accountDao.select(accountId))
                .orElseThrow(() -> new NoSuchAccountException(accountId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transfer(long fromAccountId, long toAccountId, BigDecimal amount) {
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("fromAccountId == toAccountId: " + fromAccountId);
        }
        if (Objects.requireNonNull(amount, "amount is null").compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount is not positive: " + amount);
        }
        threadSafeTransfer(fromAccountId, toAccountId, amount);
    }

    @GuardedBy("threadSafeTransfer()")
    private void transferInternal(long fromAccountId, long toAccountId, BigDecimal amount) {
        final Account fromAccount = getAccount(fromAccountId);
        final Account toAccount = getAccount(toAccountId);

        // this pre-check allows us to avoid extra database transaction: begin -> rollback in case of exceeded limit
        if (!fromAccount.canWithdraw(amount)) {
            throw new LimitExceededException(fromAccountId, amount, fromAccount.getAmount());
        }

        accountManager.transfer(fromAccount, toAccount, amount);
    }

    private void threadSafeTransfer(long fromAccountId, long toAccountId, BigDecimal amount) {
        // locks are ordered to avoid deadlocks
        final long firstId;
        final long secondId;
        if (fromAccountId < toAccountId) {
            firstId = fromAccountId;
            secondId = toAccountId;
        } else {
            firstId = toAccountId;
            secondId = fromAccountId;
        }

        final Lock firstLock = getOrCreateLock(firstId);
        final Lock secondLock = getOrCreateLock(secondId);

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                transferInternal(fromAccountId, toAccountId, amount);
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    private Lock getOrCreateLock(long accountId) {
        return locksByAccountId.asMap().computeIfAbsent(accountId, this::createLock);
    }

    private Lock createLock(Long key) {
        return new ReentrantLock();
    }

}
