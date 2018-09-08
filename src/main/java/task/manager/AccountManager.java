package task.manager;

import org.mybatis.guice.transactional.Transactional;
import task.dao.AccountDao;
import task.dao.AuditDao;
import task.model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountManager {

    private static final String UPDATED_BY = AccountManager.class.getSimpleName();

    private final AccountDao accountDao;
    private final AuditDao auditDao;

    @Inject
    public AccountManager(AccountDao accountDao, AuditDao auditDao) {
        this.accountDao = accountDao;
        this.auditDao = auditDao;
    }

    @Transactional
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        auditDao.logTransfer(fromAccount, toAccount, amount, UPDATED_BY, Timestamp.valueOf(LocalDateTime.now()));

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        accountDao.update(fromAccount);
        accountDao.update(toAccount);
    }
}
