package task.health;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import task.dao.AccountDao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseHealthCheckTest {

    @Mock
    private AccountDao dao;

    private HealthCheck healthCheck;

    @Before
    public void setUp() throws Exception {
        healthCheck = new DatabaseHealthCheck(dao);
    }

    @Test
    public void test_execute_it_must_return_healthy_result_when_ping_dao_returns_one() throws Exception {
        // Given, when
        doReturn(1).when(dao).ping();

        // Then
        assertThat(healthCheck.execute().isHealthy(), is(true));
    }

    @Test
    public void test_execute_it_must_return_unhealthy_result_when_ping_dao_returns_zero() throws Exception {
        // Given, when
        doReturn(0).when(dao).ping();

        // Then
        final HealthCheck.Result result = healthCheck.execute();
        assertThat(result.isHealthy(), not(true));
        assertThat(result.getMessage(), is(("unexpected ping result: 0")));
    }

    @Test
    public void test_execute_it_must_return_unhealthy_result_when_ping_dao_returns_two() throws Exception {
        // Given, when
        doReturn(2).when(dao).ping();

        // Then
        final HealthCheck.Result result = healthCheck.execute();
        assertThat(result.isHealthy(), not(true));
        assertThat(result.getMessage(), is(("unexpected ping result: 2")));
    }

    @Test
    public void test_execute_it_must_return_unhealthy_result_when_ping_dao_throws_exception() throws Exception {
        // Given, when
        final RuntimeException exception = new RuntimeException("test exception");
        doThrow(exception).when(dao).ping();

        // Then
        final HealthCheck.Result result = healthCheck.execute();
        assertThat(result.isHealthy(), not(true));
        assertThat(result.getError(), is(exception));
    }
}