package task.dao;

import task.model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountDaoMock implements AccountDao {

    private final ConcurrentMap<Long, Account> accountsById = new ConcurrentHashMap<>();
    private final AtomicLong generator = new AtomicLong();

    @Override
    public Account select(long accountId) {
        return accountsById.get(accountId);
    }

    @Override
    public void insert(Account account) {
        account.setId(generator.incrementAndGet());
        accountsById.put(account.getId(), account);
    }

    @Override
    public void update(Account account) {
        accountsById.put(account.getId(), account);
    }

}
