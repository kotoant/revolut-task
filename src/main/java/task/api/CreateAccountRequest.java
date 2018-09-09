package task.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * Representation class for create account operation.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class CreateAccountRequest {

    @DecimalMin("0.0")
    private BigDecimal amount;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonProperty
    public BigDecimal getAmount() {
        return amount;
    }
}
