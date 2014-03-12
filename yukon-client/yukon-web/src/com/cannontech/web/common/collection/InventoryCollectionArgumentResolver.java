package com.cannontech.web.common.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;

public class InventoryCollectionArgumentResolver {
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;

    protected boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(InventoryCollection.class);
    }

    protected Object resolveArgument(NativeWebRequest webRequest) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        return inventoryCollectionFactory.createCollection(nativeRequest);
    }
}
