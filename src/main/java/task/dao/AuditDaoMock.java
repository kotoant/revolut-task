package task.dao;

import task.model.Account;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
//TODO: move to test package
public class AuditDaoMock implements AuditDao {

    @Override
    public void logTransfer(Account fromAccount, Account toAccount, BigDecimal amount, String updatedBy, Timestamp updatedAt) {
//        TODO
    }
}
