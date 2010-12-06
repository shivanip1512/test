package com.cannontech.web.common.collection;

import com.cannontech.common.bulk.collection.inventory.YukonCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;

public class InventoryCollectionFactoryImpl extends CollectionFactoryImpl<InventoryCollectionType, YukonCollection> {

    public InventoryCollectionFactoryImpl() {
        super(InventoryCollectionType.class, YukonCollection.class);
    }
    
}