package com.marklogic.spring.batch;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class ReadCommandLineOptionsFromTest extends com.marklogic.spring.batch.AbstractSpringBatchCoreTest {

    @Test
    public void readJobNameTest() {
        String[] args = new String[] { "--job", "dummyJob123" };
        Application app = new Application();
        OptionParser parser = app.buildOptionParser();
        OptionSet options = parser.parse(args);
        assertEquals("dummyJob123", options.valueOf(Options.JOB));
    }


    @Ignore
    @Test
    public void recognizedAndUnrecognizedOptionsInOptionsFile() {
        String[] args = new String[]{
                "--job", "test",
                "--custom", "value",
                "--options_file", "src/test/resources/options/sample-options.properties"};

        Application main = new Application();
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(args);
        assertEquals("test", options.valueOf(Options.JOB));
        assertEquals("some-host", options.valueOf(Options.HOST));

        List<String> otherArgs = (List<String>) options.nonOptionArguments();
        assertEquals("--custom", otherArgs.get(0));
        assertEquals("value", otherArgs.get(1));
        assertEquals("--multi_line_property", otherArgs.get(2));
        assertEquals("Value on two lines", otherArgs.get(3));
    }

    @Test
    public void readFromMissingFile() {
        String[] args = new String[]{
                "--options_file", "src/test/resources/options/doesnt-exist.properties"};

        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        try {
            main.parseOptions(parser, args);
            fail("Expected parsing to fail because the options file doesn't exist");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

}
