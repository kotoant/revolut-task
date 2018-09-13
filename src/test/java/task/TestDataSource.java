package task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.XMLMyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import task.manager.AccountManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.inject.name.Names.bindProperties;
import static org.apache.ibatis.io.Resources.getResourceAsReader;

/**
 * This helper class creates new in-memory database each time, so there is ability to launch tests concurrently.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class TestDataSource {

    private static final AtomicLong COUNTER = new AtomicLong();

    private final List<String> initScripts;

    public TestDataSource(String... initScripts) {
        this(Arrays.asList(initScripts));
    }

    public TestDataSource(List<String> initScripts) {
        this.initScripts = initScripts;
    }

    public Injector createInjector() throws Exception {
        final Injector injector = Guice.createInjector(
                new XMLMyBatisModule() {
                    @Override
                    protected void initialize() {
                        setEnvironmentId("test");
                        install(JdbcHelper.HSQLDB_IN_MEMORY_NAMED);

                        bind(DataSource.class).toProvider(PooledDataSourceProvider.class).in(Scopes.SINGLETON);
                        bind(TransactionFactory.class).to(JdbcTransactionFactory.class).in(Scopes.SINGLETON);

                        setClassPathResource("mybatis-config.xml");

                        bind(AccountManager.class);

                        final Properties dataSourceProperties = createDataSourceProperties();
                        addProperties(dataSourceProperties);
                        bindProperties(binder(), dataSourceProperties);
                    }
                }
        );

        // prepare the test db
        DataSource dataSource = injector.getInstance(DataSource.class);
        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
        runner.setAutoCommit(true);
        runner.setStopOnError(true);
        runner.setDelimiter("/");
        for (String initScript : initScripts) {
            runner.runScript(getResourceAsReader(initScript));
        }
        runner.closeConnection();
        return injector;
    }

    private Properties createDataSourceProperties() {
        Properties myBatisProperties = new Properties();
        myBatisProperties.setProperty("mybatis.environment.id", "test");
        myBatisProperties.setProperty("JDBC.driver", "org.hsqldb.jdbcDriver");
        myBatisProperties.setProperty("JDBC.schema", "test" + String.valueOf(COUNTER.incrementAndGet()));
        myBatisProperties.setProperty("JDBC.username", "sa");
        myBatisProperties.setProperty("JDBC.password", "");
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        return myBatisProperties;
    }
}
