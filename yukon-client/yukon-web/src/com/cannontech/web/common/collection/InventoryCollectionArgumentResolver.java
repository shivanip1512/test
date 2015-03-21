package com.cannontech.web.common.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;

public class InventoryCollectionArgumentResolver {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    
    protected boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(InventoryCollection.class);
    }
    
    protected Object resolveArgument(NativeWebRequest webRequest) {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        return collectionFactory.createCollection(nativeRequest);
    }
    
}