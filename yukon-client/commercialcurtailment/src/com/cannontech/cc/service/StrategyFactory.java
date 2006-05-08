package com.cannontech.cc.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.exception.NoSuchStrategyException;

/**
 * Given the strategy key (e.g. "isocNotification") returns an instance
 * of a StrategyBase class the implements the given curtailment strategy.
 * 
 * I'm not sure if I'm in love with the way this currently works because
 * it doesn't allow the strategies to be specified completely externally.
 * A better approach may be to have each strategy type register itself
 * with this factory so that could be self contained in a seperate jar file.
 */
public class StrategyFactory implements ApplicationContextAware {

    private BeanFactory beanFactory;

    public StrategyFactory() {
        super();
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        beanFactory = applicationContext;
    }

    public StrategyBase getStrategy(Program selectedProgram) {
        String strategyName = selectedProgram.getProgramType().getStrategy();
        try {
            return (StrategyBase) beanFactory.getBean(strategyName, StrategyBase.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new NoSuchStrategyException("Strategy " + strategyName + " doesn't exist", e);
        }
    }

}
