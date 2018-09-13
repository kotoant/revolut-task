package task.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import task.TestDataSource;
import task.model.Account;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountDaoTest {

    private final TestDataSource dataSource = new TestDataSource(
            "sql/database-schema.sql", "sql/database-test-data.sql");

    @Test
    public void test_select() throws Exception {
        final AccountDao dao = dataSource.createInjector().getInstance(AccountDao.class);

        final Account account0 = dao.select(0);
        final Account account1 = dao.select(1);
        final Account account2 = dao.select(2);

        assertThat(account0).isNull();
        assertThat(account1).isEqualTo(new Account().setId(1).setAmount(new BigDecimal("123.45")));
        assertThat(account2).isEqualTo(new Account().setId(2).setAmount(new BigDecimal("678.90")));
    }

    @Test
    public void test_insert() throws Exception {
        final AccountDao dao = dataSource.createInjector().getInstance(AccountDao.class);

        final Account account = new Account().setAmount(new BigDecimal("4.5"));

        dao.insert(account);

        assertThat(account.getId()).isEqualTo(3);
        assertThat(account.getAmount()).isEqualByComparingTo(new BigDecimal(4.5));
    }

    @Test
    public void test_update() throws Exception {
        final AccountDao dao = dataSource.createInjector().getInstance(AccountDao.class);

        final Account account = new Account().setId(1).setAmount(BigDecimal.ZERO);

        dao.update(account);

        assertThat(dao.select(1)).isEqualTo(new Account().setId(1).setAmount(BigDecimal.ZERO));
    }

    @Test
    public void test_ping() throws Exception {
        final AccountDao dao = dataSource.createInjector().getInstance(AccountDao.class);

        assertThat(dao.ping()).isEqualTo(1);
    }

}