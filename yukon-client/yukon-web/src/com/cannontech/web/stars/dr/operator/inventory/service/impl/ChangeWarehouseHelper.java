package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import javax.servlet.http.HttpSession;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class ChangeWarehouseHelper extends InventoryActionsHelper {

    public class ChangeWarehouseTask extends AbstractInventoryTask {
        private int warehouseId;
        private HttpSession session;
        
        public ChangeWarehouseTask(InventoryCollection collection, YukonUserContext context, int warehouseId) {
            this.collection = collection;
            this.context = context;
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
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier inv : collection.getList()) {
                        try {
                            hardwareService.changeWarehouse(context, inv, warehouseId);
                        } catch (ObjectInOtherEnergyCompanyException e) {
                            log.error("Unable to change warehouse: " + inv, e);
                        } finally {
                            completedItems ++;
                        }
                    }
                }
            };
        }
    }

}