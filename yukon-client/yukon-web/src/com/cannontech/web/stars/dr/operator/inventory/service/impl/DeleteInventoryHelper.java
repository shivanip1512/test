package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class DeleteInventoryHelper extends InventoryActionsHelper {

    public class DeleteInventoryTask extends CollectionBasedInventoryTask {
        
        public DeleteInventoryTask(InventoryCollection collection, YukonUserContext context) {
            this.collection = collection;
            this.userContext = context;
        }
        
        public Runnable getProcessor() {
            return new Runnable() {
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
            };
        }
    }

}