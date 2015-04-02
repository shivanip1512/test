package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class DeleteInventoryHelper extends InventoryActionsHelper {
    
    /**
     * Starts an inventory task and returns the task's 
     * recent result cache identifier.
     */
    public String startTask(DeleteInventoryTask task) {
        
        executor.execute(task);
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        return taskId;
    }
    
    public class DeleteInventoryTask extends CollectionBasedInventoryTask implements Runnable {
        
        public DeleteInventoryTask(InventoryCollection collection, YukonUserContext context) {
            this.collection = collection;
            this.userContext = context;
        }
        
        @Override
        public void run() {
            for (InventoryIdentifier inv : collection.getList()) {
                if (canceled) break;
                try {
                    hardwareService.deleteHardware(userContext.getYukonUser(), true, inv.getInventoryId());
                    successCount++;
                } catch (Exception e) {
                    log.error("Unable to delete inventory: " + inv, e);
                    failedCount++;
                } finally {
                    completedItems ++;
                }
            }
        }
        
        @Override
        public MessageSourceResolvable getMessage() {
            return new YukonMessageSourceResolvable(key + "deleteInventory.label");
        }
    }
    
}