package com.cannontech.common.bulk.collection.device.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.dao.DeviceCollectionPersistenceDao;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionPersistenceService;

public class DeviceCollectionPersistenceServiceImpl implements DeviceCollectionPersistenceService {
    @Autowired DeviceCollectionPersistenceDao deviceCollectionPersistenceDao;
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
        DeviceCollectionPersistable persistable = producer.getPersistableFromCollection(collection);
        //persist collection and return the collectionId
        return deviceCollectionPersistenceDao.savePersistable(persistable);
    }
    
    @Override
    public DeviceCollection loadCollection(int collectionId) {
        DeviceCollectionPersistable persistable = deviceCollectionPersistenceDao.loadPersistable(collectionId);
        DeviceCollectionType collectionType = persistable.getCollectionType();
        DeviceCollectionProducer deviceCollectionProducer = getProducerForType(collectionType);
        return deviceCollectionProducer.getCollectionFromPersistable(persistable);
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
