package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.List;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.service.impl.NotSupportedException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.google.common.collect.Lists;

public class ChangeTypeHelper extends InventoryActionsHelper {

    public class ChangeTypeTask extends AbstractInventoryTask {
        List<InventoryIdentifier> unsupported = Lists.newArrayList();
        List<InventoryIdentifier> failed = Lists.newArrayList();
        
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
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier inv : collection.getList()) {
                        try {
                            hardwareService.changeType(context, inv, type);
                        } catch (NotSupportedException nse) {
                            unsupported.add(inv);
                        } catch (ObjectInOtherEnergyCompanyException e) {
                            failed.add(inv);
                        } finally {
                            completedItems ++;
                        }
                    }
                }
            };
        }
    }

}