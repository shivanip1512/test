package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;

public class DeleteInventoryHelper extends InventoryActionsHelper {

    public class DeleteInventoryTask extends AbstractInventoryTask {
        
        public DeleteInventoryTask(InventoryCollection collection, YukonUserContext context) {
            this.collection = collection;
            this.context = context;
        }
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier inv : collection.getList()) {
                        try {
                            hardwareService.deleteHardware(context, true, inv.getInventoryId());
                        } catch (Exception e) {
                            log.error("Unable to delete inventory: " + inv, e);
                        } finally {
                            completedItems ++;
                        }
                    }
                }
            };
        }
    }

}