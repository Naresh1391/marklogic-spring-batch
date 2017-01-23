package com.marklogic.spring.batch;

import org.junit.Test;

public class ApplicationTest {

    @Test
    public void commandLineTest() {
        String[] args = {"--marklogic.username", "bigbird", "--marklogic.jobrepository.host", "oscar"};
        Application.main(args);
    }
}
