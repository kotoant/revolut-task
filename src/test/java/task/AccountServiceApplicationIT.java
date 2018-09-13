package task;

import io.dropwizard.Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.ClassRule;
import org.junit.Test;
import task.api.CreateAccountRequest;
import task.api.CreateAccountResponse;
import task.api.GetAccountResponse;
import task.api.TransferRequest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountServiceApplicationIT {

    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE = new DropwizardAppRule<>(AccountServiceApplication.class);

    @Test
    public void test_getAccount_it_must_return_GetAccountResponse_when_account_exists() throws Exception {
        // Given, when
        final GetAccountResponse response = getAccount(1);

        // Then
        assertThat(response.getAccountId()).isEqualTo(1);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    private GetAccountResponse getAccount(long accountId) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/accounts" + "/" + accountId)
                .request()
                .get(GetAccountResponse.class);
    }

    @Test
    public void test_getAccount_it_must_return_NOT_FOUND_when_account_does_not_exist() throws Exception {
        // Given, when
        final Response response = RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/accounts" + "/100500")
                .request()
                .get();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
        assertThat(response.readEntity(String.class)).isEqualTo("No such account: 100500");
    }

    @Test
    public void test_create_account_it_must_create_account_when_request_is_correct() throws Exception {
        // Given, when
        final BigDecimal amount = new BigDecimal("100.500");
        final CreateAccountResponse createResponse = createResponseEntity(amount);

        final long accountId = createResponse.getAccountId();
        assertThat(accountId).isGreaterThan(2);

        final GetAccountResponse getResponse = getAccount(accountId);

        // Then
        assertThat(getResponse.getAccountId()).isEqualTo(accountId);
        assertThat(getResponse.getAmount()).isEqualByComparingTo(amount);
    }

    private CreateAccountResponse createResponseEntity(BigDecimal amount) {
        return createResponse(amount).readEntity(CreateAccountResponse.class);
    }

    private Response createResponse(BigDecimal amount) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/accounts/create")
                .request()
                .post(Entity.json(new CreateAccountRequest(amount)));
    }

    @Test
    public void test_create_account_it_must_return_UNPROCESSABLE_ENTITY_when_amount_is_negative() throws Exception {
        // Given
        final BigDecimal amount = new BigDecimal("-100.500");

        // When
        final Response response = createResponse(amount);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY_422);
        assertThat(response.readEntity(String.class)).contains("error", "amount", "0.0");
    }

    @Test
    public void test_transfer_it_must_return_UNPROCESSABLE_ENTITY_when_amount_is_negative() throws Exception {
        // Given
        final CreateAccountResponse from = createResponseEntity(new BigDecimal("100.500"));
        final CreateAccountResponse to = createResponseEntity(new BigDecimal("100000000"));

        // When
        final Response response = transferResponse(from.getAccountId(), to.getAccountId(), BigDecimal.TEN.negate());

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY_422);
        assertThat(response.readEntity(String.class)).contains("error", "amount", "0.0");
    }

    private Response transferResponse(long fromAccountId, long toAccountId, BigDecimal amount) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/accounts/transfer")
                .request()
                .post(Entity.json(new TransferRequest(fromAccountId, toAccountId, amount)));

    }

    @Test
    public void test_transfer_it_must_return_BAD_REQUEST_when_amount_is_zero() throws Exception {
        // Given
        final CreateAccountResponse from = createResponseEntity(new BigDecimal("100.500"));
        final CreateAccountResponse to = createResponseEntity(new BigDecimal("100000000"));

        // When
        final Response response = transferResponse(from.getAccountId(), to.getAccountId(), BigDecimal.ZERO);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(response.readEntity(String.class)).isEqualTo("amount is not positive: 0");
    }

    @Test
    public void test_transfer_it_must_return_BAD_REQUEST_when_transfer_amount_is_greater_than_source_account_amount() throws Exception {
        // Given
        final CreateAccountResponse from = createResponseEntity(new BigDecimal("100.500"));
        final CreateAccountResponse to = createResponseEntity(new BigDecimal("100000000"));

        // When
        final Response response = transferResponse(from.getAccountId(), to.getAccountId(), new BigDecimal("200"));

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(response.readEntity(String.class)).contains(
                "Failed to withdraw from account: ",
                "delta: 200 is greater than amount: 100.50000000"
        );
    }

    @Test
    public void test_transfer_it_must_transfer_when_request_is_correct() throws Exception {
        // Given
        final CreateAccountResponse from = createResponseEntity(new BigDecimal("100.500"));
        final CreateAccountResponse to = createResponseEntity(new BigDecimal("100000000"));

        // When
        final Response response = transferResponse(from.getAccountId(), to.getAccountId(), new BigDecimal("100"));

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.readEntity(String.class)).isEqualTo("OK");

        final GetAccountResponse from2 = getAccount(from.getAccountId());
        final GetAccountResponse to2 = getAccount(to.getAccountId());

        assertThat(from2.getAmount()).isEqualByComparingTo(new BigDecimal("0.5"));
        assertThat(to2.getAmount()).isEqualByComparingTo(new BigDecimal("100000100"));
    }
}