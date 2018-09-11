package task.rest;

import com.google.common.collect.ImmutableMap;
import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

/**
 * This class maps exception to {@link Response} with setting corresponding statuses for known exceptions.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@Provider
public class AccountExceptionMapper implements ExceptionMapper<Exception> {

    private final Map<Class<? extends Exception>, Status> statusesByException =
            ImmutableMap.<Class<? extends Exception>, Status>builder()
                    .put(LimitExceededException.class, Status.BAD_REQUEST)
                    .put(NoSuchAccountException.class, Status.NOT_FOUND)
                    .put(IllegalArgumentException.class, Status.BAD_REQUEST)
                    .build();

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(statusesByException.getOrDefault(exception.getClass(), Status.INTERNAL_SERVER_ERROR))
                .entity(exception.getMessage())
                .type("text/plain")
                .build();
    }
}
