package com.cannontech.common.bulk.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistenceType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceListBasedCollectionPersistable;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DeviceMemoryCollectionProducer implements DeviceCollectionProducer {

    @Autowired private DeviceDao deviceDao;

    private Cache<String, DeviceCollection> memoryMap = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.DAYS).build();

    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.memory;
    }
    
    private String getParameterName(String shortName) {
        return getSupportedType().getParameterName(shortName);
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {

        String keyName = getSupportedType().getParameterName("key");
        String key = ServletRequestUtils.getRequiredStringParameter(request, keyName);

        return memoryMap.getIfPresent(key);
    }
    
    @Override
    public DeviceCollection getCollectionFromPersistable(DeviceCollectionPersistable persistable) {
        DeviceCollectionType collectionType = persistable.getCollectionType();
        DeviceCollectionPersistenceType persistenceType = persistable.getPersistenceType();
        if(collectionType != DeviceCollectionType.memory || persistenceType != DeviceCollectionPersistenceType.DEVICE_LIST) {
            throw new IllegalArgumentException("Unable to parse device collection persistable. Collection type: " 
                + collectionType + ", Persistence type: " + persistenceType);
        }
        DeviceListBasedCollectionPersistable listPersistable = (DeviceListBasedCollectionPersistable) persistable;
        return createDeviceCollection(listPersistable.getDeviceIds());
    }
    
    @Override
    public DeviceCollectionPersistable getPersistableFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.memory) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        List<SimpleDevice> deviceList = deviceCollection.getDeviceList();
        
        return DeviceListBasedCollectionPersistable.create(DeviceCollectionType.memory, deviceList);
    }
    
    public DeviceCollection createDeviceCollection(final Iterable<YukonPao> paos) {
        
        final String key = UUID.randomUUID().toString();
        final List<SimpleDevice> devices = PaoUtils.asSimpleDeviceListFromPaos(paos);

        ListBasedDeviceCollection value = new ListBasedDeviceCollection() {
            @Override
            public DeviceCollectionType getCollectionType() {
                return getSupportedType();
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getParameterName("key"), key);

                return paramMap;
            }

            @Override
            public List<SimpleDevice> getDeviceList() {
                return devices;
            }

            @Override
            public long getDeviceCount() {
                return devices.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group.temporary");
            }

        };
        memoryMap.put(key, value);

        return value;
    }

	public DeviceCollection createDeviceCollection(List<Integer> idList) {
        final String key = UUID.randomUUID().toString();
        final List<SimpleDevice> devices = deviceDao.getYukonDeviceObjectByIds(idList);

        ListBasedDeviceCollection value = new ListBasedDeviceCollection() {
            @Override
            public DeviceCollectionType getCollectionType() {
                return getSupportedType();
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getParameterName("key"), key);

                return paramMap;
            }

            @Override
            public List<SimpleDevice> getDeviceList() {
                return devices;
            }

            @Override
            public long getDeviceCount() {
                return devices.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group.temporary");
            }

        };
        memoryMap.put(key, value);

        return value;
	}

}