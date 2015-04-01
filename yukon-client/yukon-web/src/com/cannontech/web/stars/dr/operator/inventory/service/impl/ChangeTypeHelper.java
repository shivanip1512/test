package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.service.NotSupportedException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Sets;

public class ChangeTypeHelper extends InventoryActionsHelper {
    
    public class ChangeTypeTask extends CollectionBasedInventoryTask {
        
        private Set<InventoryIdentifier> unsupported = Sets.newHashSet();
        private Set<InventoryIdentifier> successful = Sets.newHashSet();
        private Set<InventoryIdentifier> failed = Sets.newHashSet();
        
        private  YukonListEntry typeEntry;
        
        public ChangeTypeTask(InventoryCollection collection, YukonUserContext context, YukonListEntry typeEntry) {
            this.collection = collection;
            this.userContext = context;
            this.typeEntry = typeEntry;
        }
        
        public YukonListEntry getTypeEntry() {
            return typeEntry;
        }
        
        public void setTypeEntry(YukonListEntry typeEntry) {
            this.typeEntry = typeEntry;
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
                            hardwareService.changeType(userContext, inv, typeEntry);
                            successful.add(inv);
                            successCount++;
                        } catch (NotSupportedException nse) {
                            /* Original hardware type does not support the 'change type' action */
                            unsupported.add(inv);
                            unsupportedCount++;
                        } catch (ObjectInOtherEnergyCompanyException|StarsDeviceSerialNumberAlreadyExistsException e) {
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
        
        @Override
        public MessageSourceResolvable getMessage() {
            return new YukonMessageSourceResolvable(key + "changeType.label");
        }
    }
    
}