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
    public int update(Account account) {
        final Account newValue = accountsById.computeIfPresent(account.getId(), (key, value) -> account);
        if (newValue == null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int ping() {
        return 1;
    }
}
