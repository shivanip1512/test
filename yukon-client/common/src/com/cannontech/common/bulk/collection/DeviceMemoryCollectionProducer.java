package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionById;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DeviceMemoryCollectionProducer implements DeviceCollectionProducer {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupService deviceGroupService;
    
    private final Cache<String, DeviceCollection> cache = 
            CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.DAYS).build();
    
    private final static String keyBase = "yukon.common.device.bulk.bulkAction.collection.group";
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.memory;
    }
    
    private String getParameterName(String shortName) {
        return getSupportedType().getParameterName(shortName);
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest req)
            throws ServletRequestBindingException {
        
        String keyName = getSupportedType().getParameterName("key");
        String key = ServletRequestUtils.getRequiredStringParameter(req, keyName);
        
        return cache.getIfPresent(key);
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase base) {
        
        DeviceCollectionType type = base.getCollectionType();
        DeviceCollectionDbType dbType = base.getCollectionDbType();
        
        if (type != DeviceCollectionType.memory || dbType != DeviceCollectionDbType.DEVICE_LIST) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                + type + ", Persistence type: " + dbType);
        }
        DeviceCollectionById byId = (DeviceCollectionById) base;
        
        return createDeviceCollection(byId.getDeviceIds());
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection collection) {
        
        DeviceCollectionType type = collection.getCollectionType();
        if (type != DeviceCollectionType.memory) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        return DeviceCollectionById.create(DeviceCollectionType.memory, collection.getDeviceList());
    }
    
    public DeviceCollection createDeviceCollection(final Iterable<? extends YukonPao> paos) {
        
        final String key = UUID.randomUUID().toString();
        final List<SimpleDevice> devices = PaoUtils.asSimpleDeviceListFromPaos(paos);
        
        ListBasedDeviceCollection collection = buildForDevices(key, devices);
        
        return collection;
    }
    
    public DeviceCollection createDeviceCollection(List<Integer> idList) {
        
        final String key = UUID.randomUUID().toString();
        final List<SimpleDevice> devices = deviceDao.getYukonDeviceObjectByIds(idList);
        
        ListBasedDeviceCollection collection = buildForDevices(key, devices);
        
        return collection;
    }
    
    public DeviceCollection createDeviceCollection(Set<DeviceGroup> groups) {
        
        final String key = UUID.randomUUID().toString();
        
        ListBasedDeviceCollection collection = buildForGroups(key, groups);
        
        return collection;
    }
    
    private ListBasedDeviceCollection buildForDevices(final String key, final List<SimpleDevice> devices) {
        
        ListBasedDeviceCollection collection = new ListBasedDeviceCollection() {
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
            public int getDeviceCount() {
                return devices.size();
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable(keyBase + ".temporary");
            }

            @Override
            public Set<String> getErrorDevices() {
                return null;
            }

            @Override
            public int getDeviceErrorCount() {
                return 0;
            }

            @Override
            public String getUploadFileName() {
                return null;
            }

            @Override
            public String getHeader() {
                return null;
            }
            
        };
        cache.put(key, collection);
        
        return collection;
    }
    
    private ListBasedDeviceCollection buildForGroups(final String key, final Set<DeviceGroup> groups) {
        
        ListBasedDeviceCollection collection = new ListBasedDeviceCollection() {
            
            @Override
            public DeviceCollectionType getCollectionType() {
                return DeviceCollectionType.groups;
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {
                
                Map<String, String> params = new HashMap<String, String>();
                
                params.put("collectionType", DeviceCollectionType.groups.name());
                params.put(getParameterName("key"), key);
                
                return params;
            }
            
            @Override
            public List<SimpleDevice> getDeviceList() {
                
                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>();
                
                Set<SimpleDevice> devices = deviceGroupService.getDevices(groups);
                deviceList.addAll(devices);
                
                return deviceList;
            }
            
            @Override
            public List<SimpleDevice> getDevices(int start, int size) {
                
                // more than we need so we can skip past start devices and retrieve size devices
                int retrieveCount = start + size;
                
                Set<SimpleDevice> deviceSet = deviceGroupService.getDevices(groups, retrieveCount);
                
                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>(deviceSet);
                 
                return deviceList.subList(start, Math.min(retrieveCount, deviceList.size()));
            }
            
            @Override
            public int getDeviceCount() {
                return deviceGroupService.getDeviceCount(groups);
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable(keyBase + ".multi");
            }

            @Override
            public Set<String> getErrorDevices() {
                return null;
            }

            @Override
            public int getDeviceErrorCount() {
                return 0;
            }

            @Override
            public String getUploadFileName() {
                return null;
            }

            @Override
            public String getHeader() {
                return null;
            }
            
        };
        cache.put(key, collection);
        
        return collection;
    }
    
}