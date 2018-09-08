package task.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
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
}
