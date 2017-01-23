package com.marklogic.spring.batch;


import org.junit.Test;

public class ApplicationTest {

    @Test
    public void commandLineTest() {
        String[] args = {"--marklogic.host", "localhost", "--marklogic.port", "123"};
        Application.main(args);
    }
}
