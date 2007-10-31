package com.cannontech.jobs.support;

import com.cannontech.web.input.InputRoot;

public interface YukonFactoryBeanDefinition<B> {
    public B createBean();
    public InputRoot getInputs();
    public String getName();
}
