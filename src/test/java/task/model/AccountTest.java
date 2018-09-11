package task.model;

import org.junit.Before;
import org.junit.Test;
import task.exception.LimitExceededException;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountTest {

    private Account account;

    @Before
    public void setUp() throws Exception {
        account = new Account();
    }

    @Test(expected = NullPointerException.class) // Then
    public void test_set_amount_it_must_throw_null_pointer_exception_when_amount_is_null() throws Exception {
        // Given, then
        account.setAmount(null);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_set_amount_it_must_throw_illegal_argument_exception_when_amount_is_negative() throws Exception {
        // Given, then
        account.setAmount(BigDecimal.ONE.negate());
    }

    @Test
    public void test_set_amount_it_must_set_amount_when_amount_is_zero() throws Exception {
        // Given
        account.setAmount(BigDecimal.ZERO);

        // When, then
        assertThat(account.getAmount(), is(BigDecimal.ZERO));
    }

    @Test
    public void test_set_amount_it_must_set_amount_when_amount_is_positive() throws Exception {
        // Given
        account.setAmount(BigDecimal.ONE);

        // When, then
        assertThat(account.getAmount(), is(BigDecimal.ONE));
    }

    @Test(expected = NullPointerException.class) // Then
    public void test_can_withdraw_it_must_throw_null_pointer_exception_when_delta_is_null() throws Exception {
        // Given, then
        account.canWithdraw(null);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_can_withdraw_it_must_throw_illegal_argument_exception_when_delta_is_negative() throws Exception {
        // Given, then
        account.canWithdraw(BigDecimal.ONE.negate());
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_can_withdraw_it_must_throw_illegal_argument_exception_when_delta_is_zero() throws Exception {
        // Given, then
        account.canWithdraw(BigDecimal.ZERO);
    }


    @Test
    public void test_can_withdraw_it_must_return_false_when_amount_is_less_than_delta() throws Exception {
        // Given
        account.setAmount(BigDecimal.ZERO);

        // When, then
        assertThat(account.canWithdraw(BigDecimal.ONE), is(false));
    }

    @Test
    public void test_can_withdraw_it_must_return_true_when_amount_is_equal_to_delta() throws Exception {
        // Given
        account.setAmount(BigDecimal.ONE);

        // When, then
        assertThat(account.canWithdraw(BigDecimal.ONE), is(true));
    }

    @Test
    public void test_can_withdraw_it_must_return_true_when_amount_is_greater_than_delta() throws Exception {
        // Given
        account.setAmount(BigDecimal.TEN);

        // When, then
        assertThat(account.canWithdraw(BigDecimal.ONE), is(true));
    }

    @Test(expected = NullPointerException.class) // Then
    public void test_withdraw_it_must_throw_null_pointer_exception_when_delta_is_null() throws Exception {
        // Given, then
        account.withdraw(null);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_withdraw_it_must_throw_illegal_argument_exception_when_delta_is_negative() throws Exception {
        // Given, then
        account.withdraw(BigDecimal.ONE.negate());
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_withdraw_it_must_throw_illegal_argument_exception_when_delta_is_zero() throws Exception {
        // Given, then
        account.withdraw(BigDecimal.ZERO);
    }

    @Test(expected = LimitExceededException.class) // Then
    public void test_withdraw_it_must_throw_limit_exceeded_exception_when_delta_is_greater_than_amount() throws Exception {
        // Given
        account.setAmount(BigDecimal.ONE);

        // When
        account.withdraw(BigDecimal.TEN);
    }

    @Test
    public void test_withdraw_it_must_withdraw_when_delta_is_equal_to_amount() throws Exception {
        // Given
        account.setAmount(BigDecimal.ONE);

        // When
        account.withdraw(BigDecimal.ONE);

        //Then
        assertThat(account.getAmount().compareTo(BigDecimal.ZERO), is(0));
    }

    @Test
    public void test_withdraw_it_must_withdraw_when_delta_is_less_than_amount() throws Exception {
        // Given
        account.setAmount(BigDecimal.TEN);

        // When
        account.withdraw(BigDecimal.ONE);

        //Then
        assertThat(account.getAmount().compareTo(BigDecimal.valueOf(9)), is(0));
    }

    @Test(expected = NullPointerException.class) // Then
    public void test_deposit_it_must_throw_null_pointer_exception_when_delta_is_null() throws Exception {
        // Given, then
        account.deposit(null);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_deposit_it_must_throw_illegal_argument_exception_when_delta_is_negative() throws Exception {
        // Given, then
        account.deposit(BigDecimal.ONE.negate());
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_deposit_it_must_throw_illegal_argument_exception_when_delta_is_zero() throws Exception {
        // Given, then
        account.deposit(BigDecimal.ZERO);
    }

    @Test
    public void test_deposit_it_must_deposit_when_delta_is_less_than_amount() throws Exception {
        // Given
        account.setAmount(BigDecimal.TEN);

        // When
        account.deposit(BigDecimal.ONE);

        // Then
        assertThat(account.getAmount().compareTo(BigDecimal.valueOf(11)), is(0));
    }

    @Test
    public void test_deposit_it_must_deposit_when_delta_is_greater_than_amount() throws Exception {
        // Given
        account.setAmount(BigDecimal.ONE);

        // When
        account.deposit(BigDecimal.TEN);

        // Then
        assertThat(account.getAmount().compareTo(BigDecimal.valueOf(11)), is(0));
    }

    @Test
    public void test_deposit_it_must_deposit_when_delta_is_equal_to_amount() throws Exception {
        // Given
        account.setAmount(BigDecimal.TEN);

        // When
        account.deposit(BigDecimal.TEN);

        // Then
        assertThat(account.getAmount().compareTo(BigDecimal.valueOf(20)), is(0));
    }
}