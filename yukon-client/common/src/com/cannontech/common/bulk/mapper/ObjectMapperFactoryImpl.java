package com.cannontech.common.bulk.mapper;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;

/**
 * Default implementation of ObjectMapperFactory
 */
public class ObjectMapperFactoryImpl implements ObjectMapperFactory, BeanFactoryAware {
    
    private BeanFactory beanFactory;
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    
    @SuppressWarnings("unchecked")
    public ObjectMapper<String, YukonDevice> getFileImportMapper(FileMapperEnum type) {
        String beanName = type.getBeanName();
        Object bean = beanFactory.getBean(beanName, ObjectMapper.class);
        return (ObjectMapper<String, YukonDevice>) bean;
    }

}
