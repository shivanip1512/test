package com.cannontech.web.stars.dr.operator.inventoryOperations.model.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.collection.inventory.ListBasedInventoryCollection;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.CollectionProducer;
import com.google.common.collect.Lists;

public class MemoryCollectionProducer implements CollectionProducer<InventoryCollectionType, InventoryCollection> {
    
    private ConcurrentMap<String, NamedCollection> memoryMap = new ConcurrentHashMap<String, NamedCollection>();
    
    private class NamedCollection {
        private String name;
        private List<YukonInventory> collection;
        public NamedCollection(String name, List<YukonInventory> collection) {
            this.name = name;
            this.collection = collection;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<YukonInventory> getCollection() {
            return collection;
        }
        public void setCollection(List<YukonInventory> collection) {
            this.collection = collection;
        }
    };

    @Override
    public InventoryCollectionType getSupportedType() {
        return InventoryCollectionType.memory;
    }
    
    public String getParameterName(String shortName) {
        return getSupportedType() + "." + shortName;
    }

    @Override
    public InventoryCollection createCollection(HttpServletRequest request) throws ServletRequestBindingException, CollectionCreationException {
        String keyName = getSupportedType().getParameterName("key");
        String descriptionName = getSupportedType().getParameterName("description");
        
        String key = ServletRequestUtils.getStringParameter(request, keyName);
        String description = ServletRequestUtils.getStringParameter(request, descriptionName);
        
        return buildMemoryCollection(memoryMap.get(key).getCollection(), key, description);
    }
    
    public InventoryCollection createCollection(Iterator<? extends YukonInventory> inventories, String descriptionHint) {
        String key = UUID.randomUUID().toString();
        
        List<YukonInventory> yukonInventory = Lists.newArrayList();
        
        while (inventories.hasNext()) {
            yukonInventory.add(inventories.next());
        }
        
        NamedCollection namedCollection = new NamedCollection(descriptionHint, yukonInventory);
        
        memoryMap.put(key, namedCollection);
        
        return buildMemoryCollection(yukonInventory, key, descriptionHint);
    }

    public InventoryCollection buildMemoryCollection(final List<? extends YukonInventory> inventory, final String key, final String descriptionHint) {
        
        return new ListBasedInventoryCollection() {
            
            private String description = descriptionHint;

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("key"), key);
                if (StringUtils.isNotBlank(description)) {
                    paramMap.put(getParameterName("description"), description);
                }

                return paramMap;
            }

            public List<? extends YukonInventory> getInventoryList() {
                return inventory;
            }
            
            @Override
            public long getInventoryCount() {
                return inventory.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                if (description != null) {
                    return new YukonMessageSourceResolvable("yukon.common.collection.inventory.description", description);
                } else {
                    return new YukonMessageSourceResolvable("yukon.common.collection.inventory.temporary");
                }
            }

        };
    }
    
}