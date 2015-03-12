package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import javax.servlet.http.HttpSession;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.cannontech.web.util.SessionUtil;

public class ChangeDeviceStatusHelper extends InventoryActionsHelper {

    public class ChangeDeviceStatusTask extends CollectionBasedInventoryTask {
        private int statusEntryId;
        private HttpSession session;
        
        public ChangeDeviceStatusTask(InventoryCollection collection, YukonUserContext context, int statusEntryId, 
                HttpSession session) {
            this.collection = collection;
            this.userContext = context;
            this.statusEntryId = statusEntryId;
            this.session = session;
        }
        
        public HttpSession getSession() {
            return session;
        }
        
        public void setSession(HttpSession session) {
            this.session = session;
        }
        
        public int getStatusEntryId() {
            return statusEntryId;
        }
        
        public void setStatusEntryId(int statusEntryId) {
            this.statusEntryId = statusEntryId;
        }
        
        @Override
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier inv : collection.getList()) {
                        if (canceled) break;
                        try {
                            int userId = SessionUtil.getParentLoginUserId(session, userContext.getYukonUser().getUserID());
                            hardwareService.changeDeviceStatus(userContext, inv, statusEntryId, userId);
                            successCount++;
                        } catch (ObjectInOtherEnergyCompanyException|StarsDeviceSerialNumberAlreadyExistsException e) {
                            /* Inventory was probably in a member energy comany */
                            log.error("Unable to change device status: " + inv, e);
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