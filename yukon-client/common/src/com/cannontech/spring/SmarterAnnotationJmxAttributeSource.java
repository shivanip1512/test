package com.cannontech.spring;

import org.springframework.beans.annotation.AnnotationBeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.util.StringUtils;

public class SmarterAnnotationJmxAttributeSource extends
        AnnotationJmxAttributeSource {
    public ManagedResource getManagedResource(Class beanClass) throws InvalidMetadataException {
        org.springframework.jmx.export.annotation.ManagedResource ann =
                AnnotationUtils.findAnnotation((Class<?>) beanClass,org.springframework.jmx.export.annotation.ManagedResource.class);
        if (ann == null) {
            return null;
        }
        ManagedResource managedResource = new ManagedResource();
        AnnotationBeanUtils.copyPropertiesToBean(ann, managedResource);
        if (!"".equals(ann.value()) && !StringUtils.hasLength(managedResource.getObjectName())) {
            managedResource.setObjectName(ann.value());
        }
        return managedResource;
    }
    
}
