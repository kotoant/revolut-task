package task.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import task.dao.AccountDao;
import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;
import task.manager.AccountManager;
import task.model.Account;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    private AccountService accountService;
    @Mock
    private AccountManager accountManager;
    @Mock
    private AccountDao accountDao;

    @Before
    public void setUp() throws Exception {
        accountService = new AccountServiceImpl(accountManager, accountDao);
    }

    @Test
    public void test_create_it_must_create_and_insert_to_dao_account_with_zero_amount_when_amount_is_null() throws Exception {
        // Given, when
        final Account account = accountService.create(null);

        // Then
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountDao).insert(account);

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_create_it_must_create_and_insert_to_dao_account_with_zero_amount_when_amount_is_zero() throws Exception {
        // Given, when
        final Account account = accountService.create(BigDecimal.ZERO);

        // Then
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountDao).insert(account);

        verifyZeroInteractions(accountManager);
    }


    @Test
    public void test_create_it_must_create_and_insert_to_dao_account_with_given_amount_when_amount_is_not_null() throws Exception {
        // Given, when
        final Account account = accountService.create(BigDecimal.TEN);

        // Then
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        verify(accountDao).insert(account);

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_create_it_must_throw_IllegalArgumentException_when_amount_is_negative() throws Exception {
        // Given, when
        final Throwable exception = catchThrowable(() -> accountService.create(BigDecimal.TEN.negate()));

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("amount is negative: ");
        verify(accountDao, never()).insert(any(Account.class));

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_getAccount_it_must_return_account_when_dao_select_returns_account() throws Exception {
        // Given, when
        final Account actual = new Account().setId(1).setAmount(BigDecimal.TEN);
        when(accountDao.select(1)).thenReturn(actual);

        // Then
        final Account expected = accountService.getAccount(1);
        assertThat(expected).isSameAs(actual);

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_create_it_must_throw_NoSuchAccountException_when_dao_select_returns_null() throws Exception {
        // Given
        when(accountDao.select(1)).thenReturn(null);

        // When
        final Throwable exception = catchThrowable(() -> accountService.getAccount(1));

        // Then
        assertThat(exception)
                .isInstanceOf(NoSuchAccountException.class)
                .hasMessageStartingWith("No such account: ");

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_throw_IllegalArgumentException_when_fromAccountId_is_equal_to_toAccountId() throws Exception {
        // Given, when
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 1, BigDecimal.TEN));

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("fromAccountId == toAccountId: ");

        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_throw_NullPointerException_when_amount_is_null() throws Exception {
        // Given, when
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 2, null));

        // Then
        assertThat(exception)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("amount is null");

        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(accountManager);
    }


    @Test
    public void test_transfer_it_must_throw_IllegalArgumentException_when_amount_is_negative() throws Exception {
        // Given, when
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 2, BigDecimal.TEN.negate()));

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("amount is not positive: ");

        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_throw_IllegalArgumentException_when_amount_is_zero() throws Exception {
        // Given, when
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 2, BigDecimal.ZERO));

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("amount is not positive: ");

        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_throw_NoSuchAccountException_when_dao_select_returns_null_for_first_id() throws Exception {
        // Given
        when(accountDao.select(1)).thenReturn(null);

        // When
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 2, BigDecimal.ONE));

        // Then
        assertThat(exception)
                .isInstanceOf(NoSuchAccountException.class)
                .hasMessage("No such account: 1");

        verify(accountDao).select(1);
        verifyNoMoreInteractions(accountDao);
        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_throw_NoSuchAccountException_when_dao_select_returns_null_for_second_id() throws Exception {
        // Given
        when(accountDao.select(1)).thenReturn(new Account().setId(1).setAmount(BigDecimal.ONE));
        when(accountDao.select(2)).thenReturn(null);

        // When
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 2, BigDecimal.ONE));

        // Then
        assertThat(exception)
                .isInstanceOf(NoSuchAccountException.class)
                .hasMessage("No such account: 2");

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_throw_LimitExceededException_when_transfer_amount_is_greater_than_source_amount() throws Exception {
        // Given
        when(accountDao.select(1)).thenReturn(new Account().setId(1).setAmount(BigDecimal.ONE));
        when(accountDao.select(2)).thenReturn(new Account().setId(2).setAmount(BigDecimal.ZERO));

        // When
        final Throwable exception = catchThrowable(() -> accountService.transfer(1, 2, BigDecimal.TEN));

        // Then
        assertThat(exception)
                .isInstanceOf(LimitExceededException.class)
                .hasMessageStartingWith("Failed to withdraw from account: ");

        verifyZeroInteractions(accountManager);
    }

    @Test
    public void test_transfer_it_must_call_account_manager_transfer_when_amount_is_positive_accounts_exist_and_money_is_enough_without_stock() throws Exception {
        // Given
        final Account fromAccount = new Account().setId(1).setAmount(BigDecimal.TEN);
        final Account toAccount = new Account().setId(2).setAmount(BigDecimal.ONE);
        when(accountDao.select(1)).thenReturn(fromAccount);
        when(accountDao.select(2)).thenReturn(toAccount);

        // When
        accountService.transfer(1, 2, BigDecimal.TEN);

        // Then
        verify(accountManager).transfer(eq(fromAccount), eq(toAccount), eq(BigDecimal.TEN));
    }

    @Test
    public void test_transfer_it_must_call_account_manager_transfer_when_amount_is_positive_accounts_exist_and_money_is_enough_with_a_margin() throws Exception {
        // Given
        final Account fromAccount = new Account().setId(1).setAmount(BigDecimal.TEN);
        final Account toAccount = new Account().setId(2).setAmount(BigDecimal.ONE);
        when(accountDao.select(1)).thenReturn(fromAccount);
        when(accountDao.select(2)).thenReturn(toAccount);

        // When
        accountService.transfer(1, 2, BigDecimal.ONE);

        // Then
        verify(accountManager).transfer(eq(fromAccount), eq(toAccount), eq(BigDecimal.ONE));
    }
}