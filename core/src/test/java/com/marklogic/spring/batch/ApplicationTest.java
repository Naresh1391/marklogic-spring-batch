package com.marklogic.spring.batch;

import org.junit.Test;

public class ApplicationTest {

    @Test
    public void commandLineTest() throws Exception {
        String[] args = {"--job", "YourJob", "--marklogic.jobrepository.host", "oscar"};
        Application.main(args);
    }
}
