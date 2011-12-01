package com.cannontech.web.stars.dr.operator.inventory.service;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.util.Completable;
import com.cannontech.user.YukonUserContext;

public abstract class AbstractInventoryTask implements Completable {
    protected InventoryCollection collection;
    protected int completedItems;
    protected YukonUserContext context;
    protected String taskId;
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public InventoryCollection getCollection() {
        return collection;
    }
    
    public int getTotalItems() {
        return collection.getCount();
    }
    
    public int getCompletedItems() {
        return completedItems;
    }
    
    public void setCompletedItems(int completedItems) {
        this.completedItems = completedItems;
    }
    
    @Override
    public boolean isComplete() {
        return getTotalItems() == completedItems;
    }
    
    public abstract Runnable getProcessor();
}