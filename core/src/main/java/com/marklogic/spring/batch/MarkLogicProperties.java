package com.marklogic.spring.batch;

import com.marklogic.client.helper.DatabaseClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MarkLogicProperties extends DatabaseClientConfig {

    protected String database;

    @Autowired
    public MarkLogicProperties(
            @Value("${marklogic.host}") String host,
            @Value("${marklogic.port}") int port,
            @Value("${marklogic.username}") String username,
            @Value("${marklogic.password}") String password,
            @Value("${marklogic.database}") String database) {
        super(host, port, username, password);
        setDatabase(database);
    }

}
