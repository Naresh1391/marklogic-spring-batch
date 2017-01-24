package com.marklogic.spring.batch;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.MarkLogicBatchConfigurer;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

@ContextConfiguration(classes = {ApplicationContext.class} )
public abstract class AbstractSpringBatchCoreTest extends AbstractSpringTest implements ApplicationContextAware {

    private org.springframework.context.ApplicationContext applicationContext;

    protected MarkLogicBatchConfigurer marklogicBatchConfigurer;
    private JobInstanceDao jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao executionContextDao;

    public MarkLogicBatchConfigurer getMarklogicBatchConfigurer() {
        return marklogicBatchConfigurer;
    }

    @Autowired
    public void setMarklogicBatchConfigurer(MarkLogicBatchConfigurer marklogicBatchConfigurer) {
        this.marklogicBatchConfigurer = marklogicBatchConfigurer;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean("jobRepositoryDatabaseClientProvider", DatabaseClientProvider.class));
        Map<String, XccTemplate> map = applicationContext.getBeansOfType(XccTemplate.class);
        if (map.size() == 1) {
            setXccTemplate(map.values().iterator().next());
        }
        jobInstanceDao = new MarkLogicJobInstanceDao(getClient());
        jobExecutionDao = new MarkLogicJobExecutionDao(getClient());
        stepExecutionDao = new MarkLogicStepExecutionDao(getClient(), jobExecutionDao);
        executionContextDao = new MarkLogicExecutionContextDao(jobExecutionDao, stepExecutionDao);
    }

    public JobInstanceDao getJobInstanceDao() {
        return jobInstanceDao;
    }

    public JobExecutionDao getJobExecutionDao() {
        return jobExecutionDao;
    }

    public StepExecutionDao getStepExecutionDao() {
        return stepExecutionDao;
    }

    public ExecutionContextDao getExecutionContextDao() {
        return executionContextDao;
    }

}
