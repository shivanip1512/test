package com.cannontech.jobs.support;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

import com.cannontech.web.input.InputRoot;

public class SimpleYukonJobDefinition implements YukonJobDefinition, BeanFactoryAware, BeanNameAware {
    private String taskName;
    private String name;
    private String title;
    private BeanFactory beanFactory;
    private InputRoot inputs;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Object createBean() {
        Validate.isTrue(beanFactory.isPrototype(getTaskName()),
            "Bean must be defined as prototype to be a task. Could not create task: ", getTaskName());
        return beanFactory.getBean(getTaskName());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Validate.isTrue(beanFactory.isPrototype(getTaskName()),
            "Bean must be defined as prototype to be a task. Could not create job definition: ", getTaskName());
        this.beanFactory = beanFactory;
    }

    @Override
    public InputRoot getInputs() {
        return inputs;
    }

    public void setInputs(InputRoot inputs) {
        this.inputs = inputs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }
    

    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("taskName", taskName);
        builder.append("name", name);
        builder.append("title", title);
        return builder.toString();
    }
}
