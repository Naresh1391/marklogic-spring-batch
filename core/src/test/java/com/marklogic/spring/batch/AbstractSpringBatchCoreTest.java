package com.marklogic.spring.batch;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.MarkLogicBatchConfigurer;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

@ContextConfiguration(classes = {ApplicationContext.class} )
public class AbstractSpringBatchCoreTest extends AbstractSpringTest implements ApplicationContextAware {

    private org.springframework.context.ApplicationContext applicationContext;

    protected MarkLogicBatchConfigurer marklogicBatchConfigurer;

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
    }

}
