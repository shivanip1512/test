package com.cannontech.web.bulk.model;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.bulk.collection.DeviceCollection;

/**
 * Implementation class for DeviceCollectionFactory
 */
public class DeviceCollectionFactoryImpl implements DeviceCollectionFactory {

    private Map<String, DeviceCollectionProducer> collectionProducerMap = new HashMap<String, DeviceCollectionProducer>();

    public void setCollectionProducerList(
            List<DeviceCollectionProducer> collectionProducerList) {

        for (DeviceCollectionProducer producer : collectionProducerList) {
            collectionProducerMap.put(producer.getSupportedType(), producer);
        }
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        String type = request.getParameter("collectionType");

        if (collectionProducerMap.containsKey(type)) {
            DeviceCollectionProducer producer = collectionProducerMap.get(type);
            return producer.createDeviceCollection(request);
        }

        throw new InvalidParameterException("collectionType: " + type + " is not supported.");
    }

}
