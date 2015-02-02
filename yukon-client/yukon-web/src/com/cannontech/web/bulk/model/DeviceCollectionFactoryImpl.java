package com.cannontech.web.bulk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;

public class DeviceCollectionFactoryImpl implements DeviceCollectionFactory {

    private Map<DeviceCollectionType, DeviceCollectionProducer> collectionProducerMap = new HashMap<>();
    @Autowired private List<DeviceCollectionProducer> collectionProducerList;

    @PostConstruct
    public void setCollectionProducerList() {
        for (DeviceCollectionProducer producer : collectionProducerList) {
            collectionProducerMap.put(producer.getSupportedType(), producer);
        }
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request) 
            throws ServletRequestBindingException {

        String type = request.getParameter("collectionType");
        DeviceCollectionType deviceCollectionType;

        try {
            deviceCollectionType = DeviceCollectionType.valueOf(type);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("collectionType: " + type + " is not supported.");
        }

        if (collectionProducerMap.containsKey(deviceCollectionType)) {
            DeviceCollectionProducer producer = collectionProducerMap.get(deviceCollectionType);
            return producer.createDeviceCollection(request);
        }

        throw new IllegalArgumentException("collectionType: " + type + " is not supported.");
    }
}