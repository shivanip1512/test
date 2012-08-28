package com.cannontech.web.common.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;

public class InventoryCollectionFactoryImpl extends CollectionFactoryImpl<InventoryCollectionType, InventoryCollection> {

    public InventoryCollectionFactoryImpl() {
        super(InventoryCollectionType.class, InventoryCollection.class);
    }
    
    public InventoryCollection addCollectionToModelMap(HttpServletRequest request, ModelMap modelMap) throws ServletRequestBindingException {
        InventoryCollection yukonCollection = createCollection(request);
        modelMap.addAttribute("inventoryCollection", yukonCollection);
        modelMap.addAllAttributes(yukonCollection.getCollectionParameters());
        
        return yukonCollection;
    }
}