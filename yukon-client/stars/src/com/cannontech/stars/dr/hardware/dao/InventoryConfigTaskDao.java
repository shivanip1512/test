package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;

public interface InventoryConfigTaskDao {
    public InventoryConfigTask getById(int inventoryConfigTaskId);

    public InventoryConfigTask findTask(String name);

    public List<InventoryConfigTask> getAll();

    public List<InventoryConfigTask> getUnfinished();
    
    public InventoryConfigTask create(String taskName, InventoryCollection inventoryCollection);

    public void update(InventoryConfigTask task);

    public int delete(int taskId);

    public int getSuccessCount(int taskId);

    public int getFailedCount(int taskId);
    
    public List<InventoryConfigTaskItem> getItems(int maxItems);

    public void markComplete(InventoryConfigTaskItem taskItem, InventoryConfigTaskItem.Status status);

    public List<InventoryIdentifier> getSuccessFailList(int taskId, Status status);
    
}
