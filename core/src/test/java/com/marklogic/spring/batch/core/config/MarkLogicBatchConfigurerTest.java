package com.marklogic.spring.batch.core.config;

import com.marklogic.client.helper.LoggingObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableBatchProcessing
@ContextConfiguration(classes = { com.marklogic.spring.batch.ApplicationContext.class } )
public class MarkLogicBatchConfigurerTest extends LoggingObject implements ApplicationContextAware {
    
    ApplicationContext ctx;
    
    @Test
    public void doBeansExistTest() throws NoSuchBeanDefinitionException {
        for (String beanName : ctx.getBeanDefinitionNames()) {
            logger.info(beanName);
        }
        Assert.isTrue(ctx.containsBeanDefinition("marklogicBatchConfigurer"));
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
