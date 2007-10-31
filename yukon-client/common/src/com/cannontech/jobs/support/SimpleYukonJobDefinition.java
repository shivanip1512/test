package com.cannontech.jobs.support;

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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Object createBean() {
        return beanFactory.getBean(getTaskName());
    }
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    public InputRoot getInputs() {
        return inputs;
    }
    public void setInputs(InputRoot inputs) {
        this.inputs = inputs;
    }
    public String getName() {
        return name;
    }
    public void setBeanName(String name) {
        this.name = name;
    }

}
