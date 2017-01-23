package com.marklogic.spring.batch;

/**
 * Defines all recognized command-line options for the Main program.
 */
public interface Options {

    String HELP = "help";
    String OPTIONS_FILE = "options_file";

    String HOST = "marklogic.host";
    String PORT = "marklogic.port";
    String USERNAME = "marklogic.username";
    String PASSWORD = "marklogic.password";
    String DATABASE = "marklogic.database";
    String AUTHENTICATION = "auth";

    String JOB_REPOSITORY_NAME = "jr_name";
    String JOB_REPOSITORY_HOST = "marklogic.jobrepository.host";
    String JOB_REPOSITORY_PORT = "marklogic.jobrepository.port";
    String JOB_REPOSITORY_USERNAME = "marklogic.jobrepository.username";
    String JOB_REPOSITORY_PASSWORD = "marklogic.jobrepository.password";
    String JOB_REPOSITORY_DATABASE = "marklogic.jobrepository.database";
    String JOB_REPOSITORY_AUTHENTICATION = "jr_auth";

    String LIST = "list";
    String BASE_PACKAGE = "base-package";

    String CONFIG = "config";
    String JOB = "job";
    String CHUNK_SIZE = "chunk";

    String DEPLOY = "deploy_job_repo";
    String UNDEPLOY = "undeploy_job_repo";

    String JDBC_DRIVER = "jdbc_driver";
    String JDBC_URL = "jdbc_url";
    String JDBC_USERNAME = "jdbc_username";
    String JDBC_PASSWORD = "jdbc_password";
}
