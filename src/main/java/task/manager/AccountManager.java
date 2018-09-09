package task.manager;

import org.mybatis.guice.transactional.Transactional;
import task.dao.AccountDao;
import task.model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * Account manager guarantees transactional transfer.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountManager {

    private final AccountDao accountDao;

    @Inject
    public AccountManager(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Transfer money from {@code fromAccount} to {@code toAccount} in transactional manner.
     * So if exception or error occurs the whole transaction will be rolled back.
     *
     * @param fromAccount source account
     * @param toAccount   destination account
     * @param amount      money to transfer
     */
    @Transactional
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        accountDao.update(fromAccount);
        accountDao.update(toAccount);
    }
}
