package task.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Represents response for get account operation.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class GetAccountResponse {

    private final long accountId;
    private final BigDecimal amount;

    @JsonCreator
    public GetAccountResponse(@JsonProperty("accountId") long accountId, @JsonProperty("amount") BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetAccountResponse that = (GetAccountResponse) o;

        if (accountId != that.accountId) return false;
        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (accountId ^ (accountId >>> 32));
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
