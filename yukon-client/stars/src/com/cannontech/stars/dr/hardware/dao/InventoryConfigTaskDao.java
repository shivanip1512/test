package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;

public interface InventoryConfigTaskDao {
    public InventoryConfigTask getById(int inventoryConfigTaskId);

    public List<InventoryConfigTask> getAll();

    public InventoryConfigTask create(String taskName, InventoryCollection inventoryCollection);

    public void update(InventoryConfigTask task);

    public int delete(int taskId);

    public InventoryConfigTask findTask(String name);
}
