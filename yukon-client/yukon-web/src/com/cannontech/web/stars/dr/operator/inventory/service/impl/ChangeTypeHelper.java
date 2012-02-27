package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.Set;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.service.NotSupportedException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Sets;

public class ChangeTypeHelper extends InventoryActionsHelper {

    public class ChangeTypeTask extends CollectionBasedInventoryTask {
        private Set<InventoryIdentifier> unsupported = Sets.newHashSet();
        private Set<InventoryIdentifier> successful = Sets.newHashSet();
        private Set<InventoryIdentifier> failed = Sets.newHashSet();
        
        private HardwareType type;
        
        public ChangeTypeTask(InventoryCollection collection, YukonUserContext context, HardwareType type) {
            this.collection = collection;
            this.context = context;
            this.type = type;
        }
        
        public HardwareType getType() {
            return type;
        }
        
        public void setType(HardwareType type) {
            this.type = type;
        }
        
        public Set<InventoryIdentifier> getSuccessful() {
            return successful;
        }
        
        public Set<InventoryIdentifier> getFailed() {
            return failed;
        }
        
        public Set<InventoryIdentifier> getUnsupported() {
            return unsupported;
        }
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier inv : collection.getList()) {
                        if (canceled) break;
                        try {
                            hardwareService.changeType(context, inv, type);
                            successful.add(inv);
                            successCount++;
                        } catch (NotSupportedException nse) {
                            /* Original hardware type does not support the 'change type' action */
                            unsupported.add(inv);
                            unsupportedCount++;
                        } catch (ObjectInOtherEnergyCompanyException e) {
                            /* Inventory was probably in a member energy comany */
                            failed.add(inv);
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