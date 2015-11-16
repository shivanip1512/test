package com.cannontech.web.stars.dr.operator.inventory.model;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;

public abstract class CollectionBasedInventoryTask extends AbstractInventoryTask {
    
    protected InventoryCollection collection;
    
    public InventoryCollection getCollection() {
        return collection;
    }
    
    @Override
    public long getTotalItems() {
        return collection.getCount();
    }
    
}