# Account Service
Java/Scala test task for Revolut.

## Installation Prerequisites
* Git
* Maven 3.x
* Java 1.8

## Get latest sources from the repository
You can clone the repository using git with SSH:
```
git clone git@github.com:kotoant/revolut-task.git
```
Or use HTTPS:
```
git clone https://github.com/kotoant/revolut-task.git
```
Alternatively can just download [revolut-task-master.zip](https://github.com/kotoant/revolut-task/archive/master.zip) and unzip it:
```
wget https://github.com/kotoant/revolut-task/archive/master.zip -O revolut-task-master.zip
unzip revolut-task-master.zip
```

## Build the project from the source code
The build phase include unit tests:
```
mvn clean verify
```

### Running integration test
```
mvn -P integration-test clean verify
```

## Running the application
In your project directory, run this:
```
java -jar target/revolut-task-1.0-SNAPSHOT.jar
```
You should see something like the following:
```
INFO  [2018-09-09 20:27:56,300] io.dropwizard.server.DefaultServerFactory: Registering jersey handler with root path prefix: /
INFO  [2018-09-09 20:27:56,303] io.dropwizard.server.DefaultServerFactory: Registering admin handler with root path prefix: /
SET DATABASE SQL SYNTAX ORA TRUE

CREATE SEQUENCE ACCOUNT_ID_SEQ MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOCYCLE

CREATE TABLE ACCOUNT (
    ID NUMBER(19) NOT NULL,
    AMOUNT NUMBER(38, 8) NOT NULL,
    CONSTRAINT ACCOUNT_PK PRIMARY KEY (ID)
)

SET DATABASE SQL SYNTAX ORA TRUE

insert into account(id, amount) values (account_id_seq.nextval, 123.45)

insert into account(id, amount) values (account_id_seq.nextval, 678.90)

INFO  [2018-09-09 20:27:57,187] io.dropwizard.server.ServerFactory: Starting account-service
INFO  [2018-09-09 20:27:57,303] org.eclipse.jetty.setuid.SetUIDListener: Opened application@626c19cf{HTTP/1.1,[http/1.1]}{0.0.0.0:8080}
INFO  [2018-09-09 20:27:57,307] org.eclipse.jetty.setuid.SetUIDListener: Opened admin@54a2d96e{HTTP/1.1,[http/1.1]}{0.0.0.0:8081}
INFO  [2018-09-09 20:27:57,309] org.eclipse.jetty.server.Server: jetty-9.4.11.v20180605; built: 2018-06-05T18:24:03.829Z; git: d5fc0523cfa96bfebfbda19606cad384d772f04c; jvm 1.8.0_121-b13
INFO  [2018-09-09 20:27:57,977] io.dropwizard.jersey.DropwizardResourceConfig: The following paths were found for the configured resources:

    POST    /accounts/create (task.rest.AccountResource)
    POST    /accounts/transfer (task.rest.AccountResource)
    GET     /accounts/{accountId} (task.rest.AccountResource)

INFO  [2018-09-09 20:27:57,978] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@9b2dc56{/,null,AVAILABLE}
INFO  [2018-09-09 20:27:57,983] io.dropwizard.setup.AdminEnvironment: tasks = 

    POST    /tasks/log-level (io.dropwizard.servlets.tasks.LogConfigurationTask)
    POST    /tasks/gc (io.dropwizard.servlets.tasks.GarbageCollectionTask)

INFO  [2018-09-09 20:27:57,990] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@79cb8ffa{/,null,AVAILABLE}
INFO  [2018-09-09 20:27:58,175] org.eclipse.jetty.server.AbstractConnector: Started application@626c19cf{HTTP/1.1,[http/1.1]}{0.0.0.0:8080}
INFO  [2018-09-09 20:27:58,178] org.eclipse.jetty.server.AbstractConnector: Started admin@54a2d96e{HTTP/1.1,[http/1.1]}{0.0.0.0:8081}
INFO  [2018-09-09 20:27:58,178] org.eclipse.jetty.server.Server: Started @3549ms
```
The application is now listening on ports `8080` for application requests and `8081` for administration requests. If you press `^C`, the application will shut down gracefully, first closing the server socket, then waiting for in-flight requests to be processed, then shutting down the process itself.

However, while it’s up, let’s give it a whirl! [Click here to get first account][1]! [Click here to get second one][2]!

[1]: http://localhost:8080/accounts/1
[2]: http://localhost:8080/accounts/2

So, we’re getting accounts. Awesome. But that’s not all our application can do. One of the main reasons for using Dropwizard is the out-of-the-box operational tools it provides, all of which can be found [on the admin port](http://localhost:8081/).

If you click through to the [metrics resource](http://localhost:8081/metrics), you can see all of your application’s metrics represented as a JSON object.

The [threads resource](http://localhost:8081/threads) allows you to quickly get a thread dump of all the threads running in that process.
```
Hint

When a Jetty worker thread is handling an incoming HTTP request, the thread name is set to the method and URI of the request. This can be very helpful when debugging a poorly-behaving request.
```
The [healthcheck resource](http://localhost:8081/healthcheck) runs [the health check class](src/main/java/task/health/DatabaseHealthCheck.java). You should see something like this:
```
{
  "database" : {
    "healthy" : true
  },
  "deadlocks" : {
    "healthy" : true
  }
}
```
`database` here is the result of your `DatabaseHealthCheck`, which passed. `deadlocks` is a built-in health check which looks for deadlocked JVM threads and prints out a listing if any are found.

### REST API
Account service exposes three operations: 1 GET method to get account by id and 2 POST methods: create and transfer:

#### GET method: /accounts/{accountId}
Sample request:
```
$ curl http://localhost:8080/accounts/1
```
Sample response:
```
{"accountId":1,"amount":123.45000000}
```

#### POST method: /accounts/create
Sample request:
```
$ curl -H "Content-Type: application/json" -d '{"amount": 100.500}' http://localhost:8080/accounts/create
```
Sample response:
```
{"accountId":3}
```

#### POST method: /accounts/transfer
Sample request:
```
$ curl -H "Content-Type: application/json" -d '{"from": 1, "to": 2, "amount": 3.45}' http://localhost:8080/accounts/transfer
```
Sample response:
```
OK
```

## Used Frameworks and Tools
* Git as version control system
* Maven to build project
* DropWizard for application bootstraping (provides Jetty server with Jersey REST framework)
* Guice for dependeny injection
* MyBatis for JDBC operations
* mabytis-guice for proper handling of @Transactional methods
* HSQLDB for In-Memory database
* JUnit, Mockito, AssertJ, Hamcrest for testing

## Implementation Notes
The implementation assumes that there is only one instance of system-writers to the database and it is the developed service.
So all processing threads are correctly synchronized within this one service.

If we want to complicate the solution and add at least one more instance of the service that can write to the database we should use synchronization on database level.
For example optimistic locking can be used: when only updated row is locked. It can be achieved by adding one more filed to the table structure: version.
So when we want to update the row we should always check its version.
There is out-of-the-box solution for that from Hibernate.

## Further Enhancement
We can provide more dao methods for CRUD operations: getAllAccounts (with offset and/or limit), deleteById, etc.
We can also add audit information to the database the will contain whole transfer log plus history for all accounts.
There is Hibernate Envers module for that purpose.
