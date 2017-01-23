package com.marklogic.spring.batch;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import com.marklogic.spring.batch.config.MarkLogicApplicationContext;
import com.marklogic.spring.batch.config.support.BatchDatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(
        basePackages = { "com.marklogic.spring.batch" },
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {MarkLogicApplicationContext.class})}
)
@PropertySource("classpath:application.properties")
public class ApplicationContext extends LoggingObject implements EnvironmentAware {

    private Environment env;

    @Bean(name = "databaseClientProvider")
    public DatabaseClientProvider databaseClientProvider(
            @Value("${marklogic.host:localhost}") String host,
            @Value("${marklogic.port:8000}") int port,
            @Value("${marklogic.username:admin}") String username,
            @Value("${marklogic.password:admin}") String password,
            @Value("${marklogic.database:Documents}") String database) {
        DatabaseClientConfig databaseClientConfig = new DatabaseClientConfig(host, port, username, password);
        databaseClientConfig.setDatabase(database);
        logger.info("Target Database Config: " + username + ":" + host + ":" + port + "/" + database);
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }


    @Bean(name = "jobRepositoryDatabaseClientProvider")
    public DatabaseClientProvider jobRepositoryDatabaseClientProvider(
            @Value("${marklogic.jobrepository.host:localhost}") String host,
            @Value("${marklogic.jobrepository.port:8000}") int port,
            @Value("${marklogic.jobrepository.username:admin}") String username,
            @Value("${marklogic.jobrepository.password:admin}") String password,
            @Value("${marklogic.jobrepository.database:Documents}") String database) {
        DatabaseClientConfig databaseClientConfig = new DatabaseClientConfig(host, port, username, password);
        databaseClientConfig.setDatabase(database);
        logger.info("JobRepo Database Config: " + username + ":" + host + ":" + port + "/" + database);
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public XccTemplate xccTemplate(@Value("${marklogic.host:localhost}") String host,
                                   @Value("${marklogic.port:8000}") int port,
                                   @Value("${marklogic.username:admin}") String username,
                                   @Value("${marklogic.password:admin}") String password,
                                   @Value("${marklogic.database:Documents}") String database) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        username,
                        password,
                        host,
                        database));
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.env = env;
    }
}
