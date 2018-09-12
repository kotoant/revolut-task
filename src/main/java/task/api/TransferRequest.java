package task.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Represents request for transfer money from one account to another.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class TransferRequest {
    @NotNull
    private long from;
    @NotNull
    private long to;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    public TransferRequest() {
    }

    public TransferRequest(long from, long to, BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @JsonProperty
    public long getFrom() {
        return from;
    }

    @JsonProperty
    public long getTo() {
        return to;
    }

    @JsonProperty
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferRequest request = (TransferRequest) o;

        if (from != request.from) return false;
        if (to != request.to) return false;
        return amount != null ? amount.equals(request.amount) : request.amount == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
