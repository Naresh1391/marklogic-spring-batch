package com.marklogic.spring.batch;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.config.MarkLogicBatchConfigurer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.JOptCommandLinePropertySource;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

public class Application extends LoggingObject implements EnvironmentAware, ApplicationContextAware {

    private Environment env;
    private org.springframework.context.ApplicationContext context;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    public void run(String[] args) throws Exception {

        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        JOptCommandLinePropertySource ps = new JOptCommandLinePropertySource(options);
        ctx.getEnvironment().getPropertySources().addFirst(ps);
        ctx.register(com.marklogic.spring.batch.ApplicationContext.class);
        ctx.refresh();
        setJobLauncher(ctx.getBean(JobLauncher.class));
        setJobExplorer(ctx.getBean(MarkLogicBatchConfigurer.class).getJobExplorer());

        for (OptionSpec spec : options.specs()) {
            Object value = options.valueOf(spec);
            Object name = spec.options().get(0);
            logger.info(name.toString() + "=" + value.toString());
        }
        start();
        logger.info("COMPLETE");
    }

    public OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("h", Options.HELP), "Show help").forHelp();
        parser.accepts(Options.HOST, "Hostname of the target MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts(Options.PORT, "Port number of the target MarkLogic Server. There should be an XDBC App Server on this port. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class);
        parser.accepts(Options.USERNAME, "The MarkLogic user to authenticate as against the given target host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.PASSWORD, "The password for the MarkLogic user").withRequiredArg();
        parser.accepts(Options.DATABASE, "The name of the destination database. Default: The database associated with the destination App Server identified by -host and -port.").withRequiredArg();

        parser.accepts(Options.JOB, "The name of the Spring Batch Job bean to run").withRequiredArg();

        parser.accepts(Options.LIST, "List all of the Spring Configuration classes on the classpath");
        parser.accepts(Options.BASE_PACKAGE, "The optional base package to use when using --list to find Spring Configuration classes").withRequiredArg();

        parser.accepts(Options.CONFIG, "The fully qualified classname of the Spring Configuration class to register").withRequiredArg();

        parser.accepts(Options.CHUNK_SIZE, "The Spring Batch chunk size").withRequiredArg();

        parser.accepts(Options.JOB_REPOSITORY_NAME, "Name of the REST API server for the MarkLogic JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_HOST, "Hostname of the MarkLogic Server for the JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_PORT, "Port number of the App Server for the JobRepository. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class);
        parser.accepts(Options.JOB_REPOSITORY_USERNAME, "The MarkLogic user to authenticate as against JobRepository App Server").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.JOB_REPOSITORY_PASSWORD, "The password for the JobRepository MarkLogic user").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_DATABASE, "The name of the JobRepository database. Default: The database associated with the destination App Server identified by -jrHost and -jrPort.").withRequiredArg();

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

    protected JobParameters buildJobParameters(OptionSet options) {
        JobParametersBuilder jpb = new JobParametersBuilder();

        /**
         * Treat non-option arguments as job parameters. Thus, recognized options are considered
         * to be necessary for resolving @Value annotations on the given Job class, whereas
         * unrecognized options are considered to be job parameters.
         */
        List<?> nonOptionArgs = options.nonOptionArguments();
        int size = nonOptionArgs.size();
        for (int i = 0; i < size; i++) {
            String name = nonOptionArgs.get(i).toString();
            i++;
            if (i < size) {
                if (name.startsWith("--")) {
                    name = name.substring(2);
                } else if (name.startsWith("-")) {
                    name = name.substring(1);
                }
                String value = nonOptionArgs.get(i).toString();
                jpb.addString(name, value);
            }
        }

        return jpb.toJobParameters();
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    /*
 * Start a job by obtaining a combined classpath using the job launcher and
 * job paths. If a JobLocator has been set, then use it to obtain an actual
 * job, if not ask the context for it.
 */
    @SuppressWarnings("resource")
    int start() {
        Assert.state(jobLauncher != null, "A JobLauncher must be provided.  Please add one to the configuration.");
        Assert.state(jobExplorer != null,
                "A JobExplorer must be provided for a restart or start next operation.  Please add one to the configuration.");
        return 1;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
