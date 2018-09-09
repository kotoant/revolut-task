package task.model;

import task.exception.LimitExceededException;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Account provides key functionality to work with amount.
 *
 * @see Account#getAmount
 * @see Account#setAmount(BigDecimal)
 * @see Account#canWithdraw(BigDecimal)
 * @see Account#withdraw(BigDecimal)
 * @see Account#deposit(BigDecimal)
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@NotThreadSafe
public class Account {

    private long id;
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * Return account id.
     *
     * @return account id.
     */
    public long getId() {
        return id;
    }

    /**
     * Set account id to the {@code id} value.
     *
     * @param id value to set account id to
     * @return this account with updated id
     */
    public Account setId(long id) {
        this.id = id;
        return this;
    }

    /**
     * Get account amount.
     *
     * @return account amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Set account amount to the {@code amount} value.
     *
     * @param amount the value the account amount should be set to
     * @return this account with updated amount
     * @throws NullPointerException     when delta is null
     * @throws IllegalArgumentException when delta is negative
     */
    public Account setAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount is null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount is negative: " + amount);
        }
        this.amount = amount;
        return this;
    }

    /**
     * Check whether {@code delta} amount can be withdrawn from the account.
     *
     * @param delta the value to check
     * @return {@code true} if delta is less than or equal to account amount, {@code false} - otherwise.
     * @throws NullPointerException     when delta is null
     * @throws IllegalArgumentException when delta is not positive
     */
    public boolean canWithdraw(BigDecimal delta) {
        checkDelta(delta);
        return delta.compareTo(amount) <= 0;
    }

    /**
     * Withdraw account amount by the {@code delta} value.
     *
     * @param delta the value the account amount should be decreased by
     * @return this account with updated amount
     * @throws NullPointerException     when delta is null
     * @throws IllegalArgumentException when delta is not positive
     * @throws LimitExceededException   when delta is greater than amount
     */
    public Account withdraw(BigDecimal delta) {
        if (!canWithdraw(delta)) {
            throw new LimitExceededException(id, delta, amount);
        }
        amount = amount.subtract(delta);
        return this;
    }

    /**
     * Deposit account amount by the {@code delta} value.
     *
     * @param delta the value the account amount should be increased by
     * @return this account with updated amount
     * @throws NullPointerException     when delta is null
     * @throws IllegalArgumentException when delta is not positive
     */
    public Account deposit(BigDecimal delta) {
        checkDelta(delta);
        amount = amount.add(delta);
        return this;
    }

    private void checkDelta(BigDecimal delta) {
        Objects.requireNonNull(delta, "delta is null");
        if (delta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("delta is negative or equals to zero: " + amount);
        }
    }
}
