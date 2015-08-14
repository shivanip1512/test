package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService.Status;

public interface InventoryConfigTaskDao {

    InventoryConfigTask getById(int inventoryConfigTaskId);

    InventoryConfigTask findTask(String name, int energyCompanyId);

    List<InventoryConfigTask> getAll(int energyCompanyId);

    List<InventoryConfigTask> getUnfinished(int energyCompanyId);

    void update(InventoryConfigTask task);

    int delete(int taskId);

    Iterable<InventoryConfigTaskItem> getItems(int maxItems, int energyCompanyId);

    void markComplete(InventoryConfigTaskItem taskItem, Status status);

    List<InventoryIdentifier> getSuccessFailList(int taskId, Status status);

    int getTaskItemCount(int taskId, Status status);

    InventoryConfigTask create(String taskName, boolean sendInService, boolean sendOutOfService,
            InventoryCollection inventoryCollection, int energyCompanyId, LiteYukonUser user);
}
