package com.cannontech.web.bulk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;

/**
 * Implementation class for DeviceCollectionFactory
 */
public class DeviceCollectionFactoryImpl implements DeviceCollectionFactory, WebArgumentResolver {

    private Map<DeviceCollectionType, DeviceCollectionProducer> collectionProducerMap = new HashMap<DeviceCollectionType, DeviceCollectionProducer>();

    public void setCollectionProducerList(
            List<DeviceCollectionProducer> collectionProducerList) {

        for (DeviceCollectionProducer producer : collectionProducerList) {
            collectionProducerMap.put(producer.getSupportedType(), producer);
        }
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        String type = request.getParameter("collectionType");
        DeviceCollectionType deviceCollectionType = DeviceCollectionType.valueOf(type);

        if (collectionProducerMap.containsKey(deviceCollectionType)) {
            DeviceCollectionProducer producer = collectionProducerMap.get(deviceCollectionType);
            return producer.createDeviceCollection(request);
        }

        throw new IllegalArgumentException("collectionType: " + type + " is not supported.");
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest)
        throws Exception {
        Class<?> parameterType = methodParameter.getParameterType();
        if (parameterType.isAssignableFrom(DeviceCollection.class)) {
            HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
            return createDeviceCollection(nativeRequest);
        }
        return UNRESOLVED;
    }
    
    

}
