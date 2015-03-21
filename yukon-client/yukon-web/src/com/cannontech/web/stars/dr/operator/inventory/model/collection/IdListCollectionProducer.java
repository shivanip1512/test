package com.cannontech.web.stars.dr.operator.inventory.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.collection.inventory.ListBasedInventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.CollectionProducer;

public class IdListCollectionProducer implements CollectionProducer<InventoryCollectionType, InventoryCollection> {
    
    @Autowired private InventoryDao inventoryDao;
    
    public InventoryCollectionType getSupportedType() {
        return InventoryCollectionType.idList;
    }
    
    public InventoryCollection createCollection(HttpServletRequest req) throws CollectionCreationException {
        
        String param = getSupportedType().getParameterName("ids");
        final String ids;
        try {
             ids = ServletRequestUtils.getStringParameter(req, param);
        } catch (ServletRequestBindingException e) {
            throw new CollectionCreationException("Missing request param: " + param, e);
        }
        
        final List<Integer> idList = ServletUtil.getIntegerListFromString(ids);
        
        return new ListBasedInventoryCollection() {
            
            public Map<String, String> getCollectionParameters() {
                
                Map<String, String> params = new HashMap<>();
                
                params.put("collectionType", getSupportedType().name());
                params.put(param, ids);
                
                return params;
            }
            
            public List<InventoryIdentifier> getList() {
                
                List<InventoryIdentifier> inventoryList = new ArrayList<InventoryIdentifier>();
                
                for (int id : idList) {
                    InventoryIdentifier inventory = inventoryDao.getYukonInventory(id);
                    inventoryList.add(inventory);
                }
                
                return inventoryList;
            }
            
            @Override
            public int getCount() {
                return idList.size();
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.collection.inventory.idList");
            }
            
        };
    }
    
}