package task.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class CreateAccountResponse {

    private final long accountId;

    @JsonCreator
    public CreateAccountResponse(@JsonProperty("accountId") long accountId) {
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }
}
