package task.exception;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class LimitExceededException extends RuntimeException {

    private final long accountId;
    private final BigDecimal delta;
    private final BigDecimal amount;

    public LimitExceededException(long accountId, BigDecimal delta, BigDecimal amount) {
        super("Failed to withdraw from account: " + accountId + ": delta: " + delta + " is greater than amount: " + amount);
        this.accountId = accountId;
        this.delta = Objects.requireNonNull(delta, "delta is null");
        this.amount = Objects.requireNonNull(amount, "amount is null");
    }

    public BigDecimal getDelta() {
        return delta;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
