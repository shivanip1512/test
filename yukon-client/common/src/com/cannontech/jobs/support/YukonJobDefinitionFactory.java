package com.cannontech.jobs.support;

public interface YukonJobDefinitionFactory<T extends YukonTask> {
    public YukonJobDefinition<T> getJobDefinition(String jobDefinition);
}
