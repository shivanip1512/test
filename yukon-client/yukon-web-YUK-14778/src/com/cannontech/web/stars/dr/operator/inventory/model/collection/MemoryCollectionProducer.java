package com.cannontech.web.stars.dr.operator.inventory.model.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.collection.inventory.ListBasedInventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.CollectionProducer;
import com.google.common.collect.Lists;

public class MemoryCollectionProducer implements CollectionProducer<InventoryCollectionType, InventoryCollection> {
    
    private ConcurrentMap<String, InventoryCollection> cache = new ConcurrentHashMap<>();
    private static final String keyBase = "yukon.common.collection.inventory.";
    
    @Override
    public InventoryCollectionType getSupportedType() {
        return InventoryCollectionType.memory;
    }
    
    private String getParameterName(String shortName) {
        return getSupportedType().getParameterName(shortName);
    }
    
    @Override
    public InventoryCollection createCollection(HttpServletRequest req) throws CollectionCreationException {
        
        String keyName = getSupportedType().getParameterName("key");
        try {
            String key = ServletRequestUtils.getRequiredStringParameter(req, keyName);
            return cache.get(key);
        } catch (ServletRequestBindingException e) {
            throw new CollectionCreationException("Required param not found in request: " + keyName, e);
        }
        
    }
    
    public InventoryCollection createCollection(Iterator<InventoryIdentifier> identifiers, final String description) {
        
        final String key = UUID.randomUUID().toString();
        final List<InventoryIdentifier> inventoryList = Lists.newArrayList(identifiers);
        
        ListBasedInventoryCollection value = new ListBasedInventoryCollection() {
            
            public Map<String, String> getCollectionParameters() {
                
                Map<String, String> paramMap = new HashMap<String, String>();
                
                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getParameterName("key"), key);
                if (StringUtils.isNotBlank(description)) {
                    paramMap.put(getParameterName("description"), description);
                }
                
                return paramMap;
            }
            
            public List<InventoryIdentifier> getList() {
                return inventoryList;
            }
            
            @Override
            public int getCount() {
                return inventoryList.size();
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                if (description != null) {
                    return new YukonMessageSourceResolvable(keyBase + "description", description);
                } else {
                    return new YukonMessageSourceResolvable(keyBase + "temporary");
                }
            }
            
        };
        cache.put(key, value);
        
        return value;
        
    }
    
}