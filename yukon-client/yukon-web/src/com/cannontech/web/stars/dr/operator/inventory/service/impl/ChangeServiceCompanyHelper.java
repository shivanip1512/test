package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import javax.servlet.http.HttpSession;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class ChangeServiceCompanyHelper extends InventoryActionsHelper {

    public class ChangeServiceCompanyTask extends AbstractInventoryTask {
        private int serviceCompanyId;
        private HttpSession session;
        
        public ChangeServiceCompanyTask(InventoryCollection collection, YukonUserContext context, int serviceCompanyId) {
            this.collection = collection;
            this.context = context;
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
                        try {
                            hardwareService.changeServiceCompany(context, inv, serviceCompanyId);
                        } catch (ObjectInOtherEnergyCompanyException e) {
                            log.error("Unable to change service company: " + inv, e);
                        } finally {
                            completedItems ++;
                        }
                    }
                }
            };
        }
    }

}