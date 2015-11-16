package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class ChangeWarehouseHelper extends InventoryActionsHelper {
    
    /**
     * Starts an inventory task and returns the task's 
     * recent result cache identifier.
     */
    public String startTask(ChangeWarehouseTask task) {
        
        executor.execute(task);
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        return taskId;
    }
    
    public class ChangeWarehouseTask extends CollectionBasedInventoryTask implements Runnable {
        
        private int warehouseId;
        private HttpSession session;
        
        public ChangeWarehouseTask(InventoryCollection collection, YukonUserContext context, int warehouseId) {
            this.collection = collection;
            this.userContext = context;
            this.warehouseId = warehouseId;
        }
        
        public HttpSession getSession() {
            return session;
        }
        
        public void setSession(HttpSession session) {
            this.session = session;
        }
        
        public int getWarehouseId() {
            return warehouseId;
        }
        
        public void setWarehouseId(int serviceCompanyId) {
            this.warehouseId = serviceCompanyId;
        }
        
        @Override
        public void run() {
            for (InventoryIdentifier inv : collection.getList()) {
                if (canceled) break;
                try {
                    hardwareService.changeWarehouse(userContext, inv, warehouseId);
                    successCount++;
                } catch (ObjectInOtherEnergyCompanyException|StarsDeviceSerialNumberAlreadyExistsException e) {
                    /* Inventory was probably in a member energy comany */
                    log.error("Unable to change warehouse: " + inv, e);
                    failedCount++;
                } finally {
                    completedItems ++;
                }
            }
        }
        
        @Override
        public MessageSourceResolvable getMessage() {
            return new YukonMessageSourceResolvable(key + "changeWarehouse.label");
        }
    }
    
}