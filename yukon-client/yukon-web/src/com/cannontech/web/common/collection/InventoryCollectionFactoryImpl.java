package com.cannontech.web.common.collection;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;

public class InventoryCollectionFactoryImpl extends CollectionFactoryImpl<InventoryCollectionType, InventoryCollection> {

    public InventoryCollectionFactoryImpl() {
        super(InventoryCollectionType.class, InventoryCollection.class);
    }
    
}