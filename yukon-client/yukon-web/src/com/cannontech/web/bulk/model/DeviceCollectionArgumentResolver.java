package com.cannontech.web.bulk.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;

public class DeviceCollectionArgumentResolver {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;

    protected boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(DeviceCollection.class);
    }

    protected Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        return deviceCollectionFactory.createDeviceCollection(nativeRequest);
    }

}