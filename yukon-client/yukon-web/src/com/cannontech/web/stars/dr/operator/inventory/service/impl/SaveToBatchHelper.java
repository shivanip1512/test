package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.SaveToBatchController;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Lists;

public class SaveToBatchHelper extends InventoryActionsHelper {
    @Autowired protected CustomerAccountDao customerAccountDao;
    public class SaveToBatchTask extends CollectionBasedInventoryTask {

        Map<Integer,Integer> inventoryIdsToAccountIds;
        private YukonUserContext userContext;
        private YukonEnergyCompany energyCompany;
        private String routeId;
        private String groupId;
        
        public SaveToBatchTask(YukonUserContext userContext, YukonEnergyCompany energyCompany,
                InventoryCollection inventoryCollection, Map<Integer,Integer> inventoryIdsToAccountIds,
                String routeId, String groupId) {
            this.collection = inventoryCollection;
            this.userContext = userContext;
            this.energyCompany = energyCompany;
            this.routeId = routeId;
            this.groupId = groupId;
            this.inventoryIdsToAccountIds = inventoryIdsToAccountIds;
        }

        @Override
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    if (userContext == null) {
                        return;
                    }
                    if (collection == null) {
                        return;
                    }
                    
                    // Read switch_commands.txt from the stars_temp directory, or get the existing instance.
                    SwitchCommandQueue commandQueue = SwitchCommandQueue.getInstance();
                    
                    // Validate all hardware is configurable
                    List<InventoryIdentifier> hwsToConfig = Lists.newArrayList();
                    for (InventoryIdentifier identifier : collection) {
                        if (identifier.getHardwareType().isConfigurable() && !hwsToConfig.contains(identifier)) {
                            hwsToConfig.add(identifier);
                        } else {
                            failedCount++;
                            completedItems++;
                        }
                    }
                    
                    if (hwsToConfig.size() == 0) {
                        return;
                    }
                    
                    String options = null;
                    if (groupId != null && !groupId.equals(SaveToBatchController.useCurrentGroups.toString())) {
                        options = "GroupID:" + groupId;
                    }
                    if (routeId != null && !routeId.equals(SaveToBatchController.useCurrentRoutes.toString())) {
                        if (routeId.equals(SaveToBatchController.useEcDefaultRoute)) {
                            routeId = SaveToBatchController.yukonDefaultRoute;
                        }
                        if (options == null){
                            options = "RouteID:" + routeId;
                        }
                        else {
                            options += ";RouteID:" + routeId;
                        }
                    }
                    
                    // Build map of inventoryId to account Id
                    List<Integer> inventoryIds = Lists.newArrayList();
                    for (InventoryIdentifier identifier : collection) {
                        inventoryIds.add(identifier.getInventoryId());
                    }
                    Map<Integer,Integer> inventoryIdsToAccountIds = customerAccountDao.getAccountIdsByInventoryIds(inventoryIds);
                    // Add inventory to the SwitchCommandQueue
                    for (InventoryIdentifier invId : hwsToConfig) {
                        SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
                        cmd.setEnergyCompanyID(energyCompany.getEnergyCompanyId());
                        cmd.setAccountID(inventoryIdsToAccountIds.get(invId.getInventoryId()));
                        cmd.setInventoryID(invId.getInventoryId());
                        cmd.setCommandType(SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE);
                        cmd.setInfoString(options);
                        
                        commandQueue.addCommand(cmd, false);
                        successCount++;
                        completedItems++;
                    }
                    // Write out the file.
                    commandQueue.addCommand(null, true);
                }
            };
        }
    }
}