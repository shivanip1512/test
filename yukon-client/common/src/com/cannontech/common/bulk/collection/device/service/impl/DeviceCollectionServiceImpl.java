package com.cannontech.common.bulk.collection.device.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.dao.DeviceCollectionDao;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;

public class DeviceCollectionServiceImpl implements DeviceCollectionService {
    @Autowired DeviceCollectionDao deviceCollectionDao;
    @Autowired List<DeviceCollectionProducer> collectionProducerList;
    private Map<DeviceCollectionType, DeviceCollectionProducer> collectionProducerMap = new HashMap<DeviceCollectionType, DeviceCollectionProducer>();
    
    @PostConstruct
    public void setCollectionProducerList() {
        for (DeviceCollectionProducer producer : collectionProducerList) {
            collectionProducerMap.put(producer.getSupportedType(), producer);
        }
    }
    
    @Override
    public int saveCollection(DeviceCollection collection) {
        DeviceCollectionType collectionType = collection.getCollectionType();
        DeviceCollectionProducer producer = getProducerForType(collectionType);
        DeviceCollectionBase collectionBase = producer.getBaseFromCollection(collection);
        //persist collection and return the collectionId
        return deviceCollectionDao.saveCollection(collectionBase);
    }
    
    @Override
    public DeviceCollection loadCollection(int collectionId) {
        DeviceCollectionBase collection = deviceCollectionDao.loadCollection(collectionId);
        DeviceCollectionType collectionType = collection.getCollectionType();
        DeviceCollectionProducer deviceCollectionProducer = getProducerForType(collectionType);
        return deviceCollectionProducer.getCollectionFromBase(collection);
    }
    
    @Override
    public boolean deleteCollection(int collectionId) {
        return deviceCollectionDao.deleteCollection(collectionId);
    }
    
    /**
     * Finds the DeviceCollectionProducer appropriate for the specified DeviceCollectionType.
     * @throws IllegalArgumentException if there is no appropriate producer for the specified type.
     */
    private DeviceCollectionProducer getProducerForType(DeviceCollectionType collectionType) {
        if(!collectionProducerMap.containsKey(collectionType)) {
            throw new IllegalArgumentException("CollectionType " + collectionType + " is not supported.");
        }
        return collectionProducerMap.get(collectionType);
    }
}
