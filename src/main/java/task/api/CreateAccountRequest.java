package task.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * Representation class for create account operation.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class CreateAccountRequest {

    @DecimalMin("0.0")
    @Digits(integer = 30, fraction = 8)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateAccountRequest that = (CreateAccountRequest) o;

        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        return amount != null ? amount.hashCode() : 0;
    }
}
