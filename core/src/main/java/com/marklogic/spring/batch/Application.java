package com.marklogic.spring.batch;


import com.marklogic.client.helper.LoggingObject;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.JOptCommandLinePropertySource;

import java.util.Arrays;

public class Application extends LoggingObject {

    public static void main(String[] args) {
        new Application().run(args);
    }

    public void run(String[] args) {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        JOptCommandLinePropertySource ps = new JOptCommandLinePropertySource(options);
        ctx.getEnvironment().getPropertySources().addFirst(ps);
        ctx.register(ApplicationContext.class);
        ctx.refresh();

        for (OptionSpec spec : options.specs()) {
            Object value = options.valueOf(spec);
            Object name = spec.options().get(0);
            logger.info(name.toString() + "=" + value.toString());
        }


        logger.info("COMPLETE");
    }

    public OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("h", Options.HELP), "Show help").forHelp();
        parser.accepts(Options.HOST, "Hostname of the destination MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts(Options.PORT, "Port number of the destination MarkLogic Server. There should be an XDBC App Server on this port. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class);
        parser.accepts(Options.USERNAME, "The MarkLogic user to authenticate as against the given host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.PASSWORD, "The password for the MarkLogic user").withRequiredArg();
        parser.accepts(Options.DATABASE, "The name of the destination database. Default: The database associated with the destination App Server identified by -host and -port.").withRequiredArg();
        parser.accepts(Options.AUTHENTICATION, "The authentication to use for the app server on the given port").withRequiredArg();

        parser.accepts(Options.LIST, "List all of the Spring Configuration classes on the classpath");
        parser.accepts(Options.BASE_PACKAGE, "The optional base package to use when using --list to find Spring Configuration classes").withRequiredArg();

        parser.accepts(Options.CONFIG, "The fully qualified classname of the Spring Configuration class to register").withRequiredArg();
        parser.accepts(Options.JOB, "The name of the Spring Batch Job bean to run").withRequiredArg();
        parser.accepts(Options.CHUNK_SIZE, "The Spring Batch chunk size").withRequiredArg();

        parser.accepts(Options.JOB_REPOSITORY_NAME, "Name of the REST API server for the MarkLogic JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_HOST, "Hostname of the MarkLogic Server for the JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_PORT, "Port number of the App Server for the JobRepository. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class);
        parser.accepts(Options.JOB_REPOSITORY_USERNAME, "The MarkLogic user to authenticate as against JobRepository App Server").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.JOB_REPOSITORY_PASSWORD, "The password for the JobRepository MarkLogic user").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_DATABASE, "The name of the JobRepository database. Default: The database associated with the destination App Server identified by -jrHost and -jrPort.").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_AUTHENTICATION, "The authentication to use for the app server on the given JobRepository port").withRequiredArg();

        parser.accepts(Options.DEPLOY, "Include this parameter to deploy a MarkLogicJobRepository.  Requires the jr_host, jr_port, jr_username, and jr_password parameters");
        parser.accepts(Options.UNDEPLOY, "Include this parameter to undeploy a MarkLogicJobRepository.  Requires the jr_host, jr_port, jr_username, and jr_password parameters");

        parser.accepts(Options.JDBC_DRIVER, "Driver class name for connecting to a relational database").withRequiredArg();
        parser.accepts(Options.JDBC_URL, "URL for connecting to a relational database").withRequiredArg();
        parser.accepts(Options.JDBC_USERNAME, "User for connecting to a relational database").withRequiredArg();
        parser.accepts(Options.JDBC_PASSWORD, "Password for connecting to a relational database").withRequiredArg();

        parser.accepts(Options.OPTIONS_FILE, "Path to a Java-style properties file that defines additional options").withRequiredArg();

        parser.allowsUnrecognizedOptions();
        return parser;
    }




}
