package task;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import task.dao.AccountDao;
import task.dao.AccountDaoMock;
import task.service.AccountService;
import task.service.AccountServiceImpl;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountServiceApplicationTest {
    @Test
    public void test_bootstrap_it_must_create_account_service_with_mocked_dao() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(AccountService.class).to(AccountServiceImpl.class);
                bind(AccountDao.class).to(AccountDaoMock.class);
            }
        });

        final AccountService accountService = injector.getInstance(AccountService.class);
    }
}
