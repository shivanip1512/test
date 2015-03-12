package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import javax.servlet.http.HttpSession;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class ChangeServiceCompanyHelper extends InventoryActionsHelper {

    public class ChangeServiceCompanyTask extends CollectionBasedInventoryTask {
        private int serviceCompanyId;
        private HttpSession session;
        
        public ChangeServiceCompanyTask(InventoryCollection collection, YukonUserContext context, int serviceCompanyId) {
            this.collection = collection;
            this.userContext = context;
            this.serviceCompanyId = serviceCompanyId;
        }
        
        public HttpSession getSession() {
            return session;
        }
        
        public void setSession(HttpSession session) {
            this.session = session;
        }
        
        public int getServiceCompanyId() {
            return serviceCompanyId;
        }
        
        public void setServiceCompanyId(int serviceCompanyId) {
            this.serviceCompanyId = serviceCompanyId;
        }
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier inv : collection.getList()) {
                        if (canceled) break;
                        try {
                            hardwareService.changeServiceCompany(userContext, inv, serviceCompanyId);
                            successCount++;
                        } catch (ObjectInOtherEnergyCompanyException|StarsDeviceSerialNumberAlreadyExistsException e) {
                            /* Inventory was probably in a member energy comany */
                            log.error("Unable to change service company: " + inv, e);
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