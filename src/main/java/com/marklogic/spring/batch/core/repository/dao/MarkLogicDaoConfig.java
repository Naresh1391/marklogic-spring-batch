package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.marklogic.client.helper.DatabaseClientProvider;

@Configuration
@Import( com.marklogic.client.spring.BasicConfig.class )
public class MarkLogicDaoConfig {
	
	@Autowired
	public DatabaseClientProvider databaseClientProvider;
	
	@Bean
	public JobInstanceDao jobInstanceDao() throws Exception {
		return new MarkLogicJobInstanceDao(databaseClientProvider.getDatabaseClient());
	}
	
	@Bean
	public JobExecutionDao jobExecutionDao() throws Exception {
		return new MarkLogicJobExecutionDao(databaseClientProvider.getDatabaseClient());
	}
	
	@Bean
	public StepExecutionDao stepExecutionDao() throws Exception {
		MarkLogicStepExecutionDao stepExecutionDao = new MarkLogicStepExecutionDao(databaseClientProvider.getDatabaseClient());
		stepExecutionDao.setJobExecutionDao(jobExecutionDao());
		return stepExecutionDao;
	}
	
	@Bean
	public ExecutionContextDao executionContextDao() throws Exception {
		return new MarkLogicExecutionContextDao(jobExecutionDao(), stepExecutionDao());
	}
	
	@Bean
	public JobRepository jobRepository() throws Exception {
		return new SimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
	}

	@Bean
	public JobExplorer jobExplorer() throws Exception {
		return new SimpleJobExplorer(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
	}
	
}
