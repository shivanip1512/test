package com.cannontech.web.bulk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;

public class DeviceCollectionFactoryImpl implements DeviceCollectionFactory {

    private Map<DeviceCollectionType, DeviceCollectionProducer> typeToProducers = new HashMap<>();
    @Autowired private List<DeviceCollectionProducer> producers;

    @PostConstruct
    public void setCollectionProducerList() {
        for (DeviceCollectionProducer producer : producers) {
            typeToProducers.put(producer.getSupportedType(), producer);
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
        
        if (typeToProducers.containsKey(deviceCollectionType)) {
            DeviceCollectionProducer producer = typeToProducers.get(deviceCollectionType);
            return producer.createDeviceCollection(request);
        }

        throw new IllegalArgumentException("collectionType: " + type + " is not supported.");
    }
}