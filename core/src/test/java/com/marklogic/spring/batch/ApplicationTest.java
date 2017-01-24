package com.marklogic.spring.batch;

import org.junit.Test;

public class ApplicationTest {

    @Test
    public void commandLineTest() throws Exception {
        String[] args = {"--job", "YourJob", "--jobrepository.host", "oscar", "--output_collections", "monster"};
        Application.main(args);
    }
}
