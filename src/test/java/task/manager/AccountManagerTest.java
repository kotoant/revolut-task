package task.manager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import task.dao.AccountDao;
import task.model.Account;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountManagerTest {

    private AccountManager manager;
    @Mock
    private AccountDao accountDao;
    @Mock
    private Account accountFrom;
    @Mock
    private Account accountTo;


    @Before
    public void setUp() throws Exception {
        manager = new AccountManager(accountDao);
    }

    @Test
    public void test_transfer() throws Exception {
    }

}