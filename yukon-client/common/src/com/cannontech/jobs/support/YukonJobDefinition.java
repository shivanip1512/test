package com.cannontech.jobs.support;


public interface YukonJobDefinition<T extends YukonTask> extends YukonFactoryBeanDefinition<T> {
    public String getTitle();
}
