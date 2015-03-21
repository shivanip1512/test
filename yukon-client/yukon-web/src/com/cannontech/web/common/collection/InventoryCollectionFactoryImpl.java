package com.cannontech.web.common.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;

public class InventoryCollectionFactoryImpl extends CollectionFactoryImpl<InventoryCollectionType, InventoryCollection> {
    
    public InventoryCollectionFactoryImpl() {
        super(InventoryCollectionType.class);
    }
    
    public InventoryCollection addCollectionToModelMap(HttpServletRequest req, ModelMap model)
    throws CollectionCreationException {
        
        InventoryCollection collection = createCollection(req);
        model.addAttribute("inventoryCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        return collection;
    }
    
}