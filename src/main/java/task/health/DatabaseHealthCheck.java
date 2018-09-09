package task.health;

import com.codahale.metrics.health.HealthCheck;
import task.dao.AccountDao;

import javax.inject.Inject;

/**
 * This class checks that database is reachable and operational.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class DatabaseHealthCheck extends HealthCheck {

    private final AccountDao dao;

    @Inject
    public DatabaseHealthCheck(AccountDao dao) {
        this.dao = dao;
    }

    @Override
    protected Result check() throws Exception {
        final int ping = dao.ping();
        if (ping == 1) {
            return Result.healthy();
        }
        return Result.unhealthy("unexpected ping result: %d", ping);
    }
}
