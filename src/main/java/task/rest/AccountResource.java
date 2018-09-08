package task.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    private final AccountService accountService;

    @Inject
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Path("/create")
    public Response create(@Valid @NotNull CreateAccountRequest request) {
        final Account account = accountService.create(request.getAmount());
        return Response.status(Response.Status.CREATED)
                .entity(new CreateAccountResponse(account.getId()))
                .build();
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@NotNull @PathParam("accountId") long accountId) {
        final Account account = accountService.getAccount(accountId);
        return Response.status(Response.Status.OK)
                .entity(new GetAccountResponse(account.getId(), account.getAmount()))
                .build();
    }

    @POST
    @Path("/transfer")
    public Response transfer(@Valid @NotNull TransferRequest request) {
        accountService.transfer(request.getFrom(), request.getTo(), request.getAmount());
        return Response.status(Response.Status.OK).entity("OK").build();
    }
}
