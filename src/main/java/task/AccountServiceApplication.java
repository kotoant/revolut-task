package task;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.XMLMyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import task.dao.AccountDao;
import task.health.DatabaseHealthCheck;
import task.manager.AccountManager;
import task.rest.AccountExceptionMapper;
import task.rest.AccountResource;
import task.service.AccountService;
import task.service.AccountServiceImpl;

import javax.sql.DataSource;
import java.util.Properties;

import static com.google.inject.name.Names.bindProperties;
import static org.apache.ibatis.io.Resources.getResourceAsReader;

/**
 * Main application class. Contains bootstrap logic and configuration code.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class AccountServiceApplication extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[]{"server", "/resource-config.yaml"};
        }
        new AccountServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "account-service";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        // Search config file on classpath
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());

        super.initialize(bootstrap);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(AccountService.class).to(AccountServiceImpl.class);
                    }
                },
                new XMLMyBatisModule() {
                    @Override
                    protected void initialize() {
                        setEnvironmentId("test");
                        install(JdbcHelper.HSQLDB_IN_MEMORY_NAMED);

                        bind(DataSource.class).toProvider(PooledDataSourceProvider.class).in(Scopes.SINGLETON);
                        bind(TransactionFactory.class).to(JdbcTransactionFactory.class).in(Scopes.SINGLETON);

                        setClassPathResource("mybatis-config.xml");

                        // bind classes with transactional method(s)
                        bind(AccountManager.class);

                        bindProperties(binder(), createDataSourceProperties());
                    }
                }
        );

        // prepare the test db
        DataSource dataSource = injector.getInstance(DataSource.class);
        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
        runner.setAutoCommit(true);
        runner.setStopOnError(true);
        runner.runScript(getResourceAsReader("sql/database-schema.sql"));
        runner.runScript(getResourceAsReader("sql/database-test-data.sql"));
        runner.closeConnection();

        final DatabaseHealthCheck healthCheck = new DatabaseHealthCheck(injector.getInstance(AccountDao.class));
        environment.healthChecks().register("database", healthCheck);

        final AccountResource resource = injector.getInstance(AccountResource.class);
        environment.jersey().register(resource);

        final AccountExceptionMapper exceptionMapper = new AccountExceptionMapper();
        environment.jersey().register(exceptionMapper);
    }

    protected static Properties createDataSourceProperties() {
        Properties myBatisProperties = new Properties();
        myBatisProperties.setProperty("mybatis.environment.id", "test");
        myBatisProperties.setProperty("JDBC.username", "sa");
        myBatisProperties.setProperty("JDBC.password", "");
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        return myBatisProperties;
    }
}
