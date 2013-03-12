package com.cannontech.web.bulk.model.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class DeviceMemoryCollectionProducer implements DeviceCollectionProducer {
    
    private Cache<String, DeviceCollection> memoryMap = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.DAYS).build();
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.memory;
    }
    
    private String getParameterName(String shortName) {
        return getSupportedType().getParameterName(shortName);
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        String keyName = getSupportedType().getParameterName("key");
        String key = ServletRequestUtils.getRequiredStringParameter(request, keyName);
        
        return memoryMap.getIfPresent(key);
    }
    
    public DeviceCollection createDeviceCollection(Iterator<PaoIdentifier> paos) {
        final String key = UUID.randomUUID().toString();
        
        final List<PaoIdentifier> paoIdentifiers = Lists.newArrayList(paos);
        
        ListBasedDeviceCollection value = new ListBasedDeviceCollection() {
            
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getParameterName("key"), key);

                return paramMap;
            }

            public List<SimpleDevice> getDeviceList() {
                return PaoUtils.asSimpleDeviceList(paoIdentifiers);
            }
            
            @Override
            public long getDeviceCount() {
                return paoIdentifiers.size();
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