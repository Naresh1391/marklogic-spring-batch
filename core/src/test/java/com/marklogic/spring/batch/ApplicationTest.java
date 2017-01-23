package com.marklogic.spring.batch;


import org.junit.Test;

public class ApplicationTest {

    @Test
    public void commandLineTest() {
        String[] args = {"--host", "localhost123", "--port", "123"};
        Application.main(args);
    }
}
