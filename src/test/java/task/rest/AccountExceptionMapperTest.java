package task.rest;

import org.junit.Before;
import org.junit.Test;
import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountExceptionMapperTest {

    private ExceptionMapper<Exception> exceptionMapper;

    @Before
    public void setUp() throws Exception {
        exceptionMapper = new AccountExceptionMapper();
    }

    @Test
    public void test_response_it_must_map_LimitExceededException_to_BAD_REQUEST() throws Exception {
        // Given
        final Exception exception = new LimitExceededException(1, BigDecimal.ONE, BigDecimal.ZERO);

        // When
        final Response response = exceptionMapper.toResponse(exception);

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.BAD_REQUEST));
        assertThat(response.getEntity(), is(exception.getMessage()));
    }

    @Test
    public void test_response_it_must_map_NoSuchAccountException_to_NOT_FOUND() throws Exception {
        // Given
        final Exception exception = new NoSuchAccountException(1);

        // When
        final Response response = exceptionMapper.toResponse(exception);

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.NOT_FOUND));
        assertThat(response.getEntity(), is(exception.getMessage()));
    }

    @Test
    public void test_response_it_must_map_IllegalArgumentException_to_BAD_REQUEST() throws Exception {
        // Given
        final Exception exception = new IllegalArgumentException("test message");

        // When
        final Response response = exceptionMapper.toResponse(exception);

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.BAD_REQUEST));
        assertThat(response.getEntity(), is("test message"));
    }

    @Test
    public void test_response_it_must_map_NullPointerException_to_BAD_REQUEST() throws Exception {
        // Given
        final Exception exception = new NullPointerException("test message");

        // When
        final Response response = exceptionMapper.toResponse(exception);

        // Then
        assertThat(response.getStatusInfo(), is(Response.Status.INTERNAL_SERVER_ERROR));
        assertThat(response.getEntity(), is("test message"));
    }
}