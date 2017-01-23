package com.marklogic.spring.batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationProperties {

    private List<String> jobNames;
    private String jobName;
    private int chunkSize;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(@Value("${spring.batch.job}") String jobName) {
        this.jobName = jobName;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

}
