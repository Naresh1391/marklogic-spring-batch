package com.marklogic.spring.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { ApplicationContext.class })
public class ApplicationTest {

    @Test
    public void commandLineTest() {
        String[] args = {"--marklogic.username", "bigbird", "--marklogic.jobrepository.host", "oscar"};
        Application.main(args);
    }
}
