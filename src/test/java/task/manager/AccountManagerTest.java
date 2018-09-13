package task.manager;

import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import task.TestDataSource;
import task.dao.AccountDao;
import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;
import task.model.Account;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountManagerTest {

    private final TestDataSource dataSource = new TestDataSource(
            "sql/database-schema.sql", "sql/database-test-data.sql");

    @Test
    public void test_transfer_it_must_rollback_transaction_when_first_account_does_not_exist() throws Exception {
        final Injector injector = dataSource.createInjector();
        final AccountManager accountManager = injector.getInstance(AccountManager.class);
        final AccountDao accountDao = injector.getInstance(AccountDao.class);

        // Given
        final Account fromAccount = new Account().setId(3).setAmount(BigDecimal.TEN);
        final Account toAccount = spy(accountDao.select(1));

        // When
        final Throwable exception = catchThrowable(() -> accountManager.transfer(fromAccount, toAccount, BigDecimal.ONE));

        // Then
        assertThat(exception)
                .isInstanceOf(NoSuchAccountException.class)
                .hasMessage("No such account: 3");

        verifyZeroInteractions(toAccount);

        assertThat(accountDao.select(1)).isEqualTo(new Account().setId(1).setAmount(new BigDecimal("123.45")));
    }

    @Test
    public void test_transfer_it_must_rollback_transaction_when_second_account_does_not_exist() throws Exception {
        final Injector injector = dataSource.createInjector();
        final AccountManager accountManager = injector.getInstance(AccountManager.class);
        final AccountDao accountDao = injector.getInstance(AccountDao.class);

        // Given
        final Account fromAccount = spy(accountDao.select(2));
        final Account toAccount = new Account().setId(4).setAmount(BigDecimal.TEN);

        // When
        final Throwable exception = catchThrowable(() -> accountManager.transfer(fromAccount, toAccount, BigDecimal.ONE));

        // Then
        assertThat(exception)
                .isInstanceOf(NoSuchAccountException.class)
                .hasMessage("No such account: 4");

        verify(fromAccount).withdraw(eq(BigDecimal.ONE));

        assertThat(accountDao.select(2)).isEqualTo(new Account().setId(2).setAmount(new BigDecimal("678.90")));
    }

    @Test
    public void test_transfer_it_must_rollback_transaction_when_limit_exceeded() throws Exception {
        final Injector injector = dataSource.createInjector();
        final AccountManager accountManager = injector.getInstance(AccountManager.class);
        final AccountDao accountDao = injector.getInstance(AccountDao.class);

        // Given
        final Account fromAccount = spy(accountDao.select(1));
        final Account toAccount = spy(accountDao.select(2));

        // When
        final Throwable exception = catchThrowable(() -> accountManager.transfer(fromAccount, toAccount, new BigDecimal("9999999999")));

        // Then
        assertThat(exception)
                .isInstanceOf(LimitExceededException.class)
                .hasMessageStartingWith("Failed to withdraw from account: 1");

        verify(fromAccount).withdraw(eq(new BigDecimal("9999999999")));

        verifyZeroInteractions(toAccount);
    }

    @Test
    public void test_transfer_it_must_tranfer_when_accounts_exist_and_money_is_enough() throws Exception {
        final Injector injector = dataSource.createInjector();
        final AccountManager accountManager = injector.getInstance(AccountManager.class);
        final AccountDao accountDao = injector.getInstance(AccountDao.class);

        // Given
        final Account fromAccount = spy(accountDao.select(1));
        final Account toAccount = spy(accountDao.select(2));

        // When
        accountManager.transfer(fromAccount, toAccount, BigDecimal.TEN);

        // Then
        verify(fromAccount).withdraw(eq(BigDecimal.TEN));
        verify(toAccount).deposit(eq(BigDecimal.TEN));

        assertThat(accountDao.select(1)).isEqualTo(new Account().setId(1).setAmount(new BigDecimal("113.45")));
        assertThat(accountDao.select(2)).isEqualTo(new Account().setId(2).setAmount(new BigDecimal("688.90")));
    }
}