package com.marklogic.spring.batch;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.config.support.BatchDatabaseClientProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationContext extends LoggingObject implements EnvironmentAware {

    private Environment env;

    public DatabaseClientConfig targetDatabaseConfig(
            @Qualifier("markLogicProperties")MarkLogicProperties marklogicProperties) {
        DatabaseClientConfig config = new DatabaseClientConfig(
                env.getProperty(Options.HOST),
                Integer.parseInt(env.getProperty(Options.PORT)),
                env.getProperty(Options.USERNAME),
                env.getProperty(Options.PASSWORD)
        );

        String db = env.getProperty(Options.DATABASE);
        if (db != null) {
            config.setDatabase(db);
        }

        logger.info("Connecting to MarkLogic via: " + config);
        return config;
    }

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.env = env;
    }
}
