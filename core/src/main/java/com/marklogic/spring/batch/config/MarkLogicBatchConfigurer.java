package com.marklogic.spring.batch.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;

@Component
public class MarkLogicBatchConfigurer implements BatchConfigurer {
    
    private final static Logger logger = LoggerFactory.getLogger(MarkLogicBatchConfigurer.class);
    private DatabaseClient databaseClient;
    private JobRepository jobRepository;
    private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;
    private PlatformTransactionManager transactionManager;

    private MarkLogicJobInstanceDao jobInstanceDao;
    private MarkLogicJobExecutionDao jobExecutionDao;
    private MarkLogicStepExecutionDao stepExecutionDao;
    private MarkLogicExecutionContextDao executionDao;

    protected MarkLogicBatchConfigurer() {}
    
    @Autowired
    public MarkLogicBatchConfigurer(@Qualifier("jobRepositoryDatabaseClientProvider") DatabaseClientProvider databaseClientProvider) {
        this.databaseClient = databaseClientProvider.getDatabaseClient();
        jobInstanceDao = new MarkLogicJobInstanceDao(databaseClient);
        jobExecutionDao = new MarkLogicJobExecutionDao(databaseClient);
        stepExecutionDao = new MarkLogicStepExecutionDao(databaseClient, jobExecutionDao);
        executionDao = new MarkLogicExecutionContextDao(jobExecutionDao, stepExecutionDao);
    }
    
    @PostConstruct
    public void initialize() throws Exception {
        if(databaseClient == null) {
            logger.warn("No DatabaseClient was provided...using a Map based JobRepository");
    
            if(this.transactionManager == null) {
                this.transactionManager = new ResourcelessTransactionManager();
            }
    
            MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.transactionManager);
            jobRepositoryFactory.afterPropertiesSet();
            this.jobRepository = jobRepositoryFactory.getObject();
    
            MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
            jobExplorerFactory.afterPropertiesSet();
            this.jobExplorer = jobExplorerFactory.getObject();
        } else {
            this.transactionManager = new ResourcelessTransactionManager();
            this.jobRepository = createJobRepository();
            this.jobExplorer = createJobExplorer();
        }
        this.jobLauncher = createJobLauncher();
    }
    
    protected JobRepository createJobRepository() throws Exception {
        return new MarkLogicSimpleJobRepository(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionDao);
    }
    
    protected JobExplorer createJobExplorer() throws Exception {
        return new SimpleJobExplorer(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionDao);
    }
    
    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Override
    public JobRepository getJobRepository() throws Exception {
        return jobRepository;
    }
    
    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return transactionManager;
    }
    
    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return jobLauncher;
    }
    
    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return jobExplorer;
    }
    
}
