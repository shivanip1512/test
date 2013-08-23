package com.cannontech.messaging.serialization.thrift.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class ContextAwareTestBase {

    protected ApplicationContext appContext;

    public ContextAwareTestBase() {
        setUpTest(getContextUri());
    }

    protected abstract String[] getContextUri();

    protected void setUpTest(String... contextURI) {
        appContext = new ClassPathXmlApplicationContext(contextURI);
    }
}
