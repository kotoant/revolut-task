package task.rest;

import task.api.CreateAccountRequest;
import task.api.CreateAccountResponse;
import task.api.GetAccountResponse;
import task.api.TransferRequest;
import task.model.Account;
import task.service.AccountService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This REST resource exposes {@link AccountService} API. Exceptions are handled by {@link AccountExceptionMapper}.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 * @see CreateAccountRequest
 * @see CreateAccountResponse
 * @see GetAccountResponse
 * @see TransferRequest
 * @see AccountExceptionMapper
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountService accountService;

    @Inject
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates new account.
     *
     * @param request {@link CreateAccountRequest}
     * @return {@link Response} with Status.CREATED status and {@link CreateAccountResponse} entity in case of success.
     */
    @POST
    @Path("/create")
    public Response create(@Valid @NotNull CreateAccountRequest request) {
        final Account account = accountService.create(request.getAmount());
        return Response.ok(new CreateAccountResponse(account.getId())).build();
    }

    /**
     * Get account with given {@code accountId}.
     *
     * @param accountId account id
     * @return {@link Response} with Status.OK status and {@link GetAccountResponse} entity in case of success.
     */
    @GET
    @Path("/{accountId}")
    public Response getAccount(@NotNull @PathParam("accountId") long accountId) {
        final Account account = accountService.getAccount(accountId);
        return Response.ok(new GetAccountResponse(account.getId(), account.getAmount())).build();
    }

    /**
     * Transfers money from one account to another.
     *
     * @param request {@link TransferRequest}
     * @return {@link Response} with Status.OK status and "OK" entity in case of success.
     */
    @POST
    @Path("/transfer")
    public Response transfer(@Valid @NotNull TransferRequest request) {
        accountService.transfer(request.getFrom(), request.getTo(), request.getAmount());
        return Response.ok("OK").build();
    }
}
