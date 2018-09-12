package task.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import task.api.CreateAccountRequest;
import task.api.CreateAccountResponse;
import task.api.GetAccountResponse;
import task.api.TransferRequest;
import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;
import task.model.Account;
import task.service.AccountService;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {

    private AccountResource accountResource;
    @Mock
    private AccountService accountService;
    private Account account;

    @Before
    public void setUp() throws Exception {
        accountResource = new AccountResource(accountService);
        account = new Account().setId(1).setAmount(BigDecimal.TEN);
    }

    @Test
    public void test_create_it_must_return_response_with_account_id_when_service_creates_account() throws Exception {
        // Given
        when(accountService.create(account.getAmount())).thenReturn(account);

        // When
        final Response response = accountResource.create(new CreateAccountRequest(account.getAmount()));

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.OK));
        assertThat(response.getEntity(), is(new CreateAccountResponse(account.getId())));
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void test_create_it_must_throw_exception_with_account_id_when_service_throws_exception() throws Exception {
        // Given
        when(accountService.create(account.getAmount())).thenThrow(IllegalArgumentException.class);

        // When
        accountResource.create(new CreateAccountRequest(account.getAmount()));
    }

    @Test
    public void test_getAccount_it_must_return_response_with_account_id_and_amount_when_service_returns_account() throws Exception {
        // Given
        when(accountService.getAccount(account.getId())).thenReturn(account);

        // When
        final Response response = accountResource.getAccount(account.getId());

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.OK));
        assertThat(response.getEntity(), is(new GetAccountResponse(account.getId(), account.getAmount())));
    }

    @Test(expected = NoSuchAccountException.class) // Then
    public void test_getAccount_it_must_throw_exception_when_service_throws_exception() throws Exception {
        // Given
        when(accountService.getAccount(account.getId())).thenThrow(NoSuchAccountException.class);

        // When
        accountResource.getAccount(account.getId());
    }


    @Test
    public void test_transfer_it_must_return_ok_response_when_service_transfers() throws Exception {
        // Given
        final TransferRequest request = new TransferRequest(1, 2, BigDecimal.TEN);

        // When
        final Response response = accountResource.transfer(request);

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.OK));
        assertThat(response.getEntity(), is("OK"));
    }

    @Test(expected = LimitExceededException.class) // Then
    public void test_transfer_it_must_throw_exception_when_service_throws_exception() throws Exception {
        // Given
        final TransferRequest request = new TransferRequest(1, 2, BigDecimal.TEN);
        doThrow(LimitExceededException.class).when(accountService).transfer(request.getFrom(), request.getTo(), request.getAmount());

        // When
        accountResource.transfer(request);
    }
}
