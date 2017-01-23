package com.marklogic.spring.batch;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationProperties {

    private List<String> jobNames;
    private String jobName;
    private int chunkSize;

}
