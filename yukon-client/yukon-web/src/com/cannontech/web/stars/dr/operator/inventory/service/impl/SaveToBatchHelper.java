package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.web.stars.dr.operator.inventory.model.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.SaveToBatchInfo;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Lists;

public class SaveToBatchHelper extends InventoryActionsHelper {
    
    @Autowired protected CustomerAccountDao customerAccountDao;
    
    /**
     * Starts an inventory task and returns the task's 
     * recent result cache identifier.
     */
    public String startTask(SaveToBatchTask task) {
        
        executor.execute(task);
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        return taskId;
    }
    
    public class SaveToBatchTask extends CollectionBasedInventoryTask implements Runnable {
        
        Map<Integer,Integer> inventoryIdsToAccountIds;
        private int ecId;
        private SaveToBatchInfo saveToBatchInfo;
        
        public SaveToBatchTask(int ecId, InventoryCollection collection, Map<Integer, Integer> inventoryIdsToAccountIds,
                SaveToBatchInfo saveToBatchInfo) {
            
            this.collection = collection;
            this.ecId = ecId;
            this.inventoryIdsToAccountIds = inventoryIdsToAccountIds;
            this.saveToBatchInfo = saveToBatchInfo;
        }
        
        @Override
        public void run() {
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
            if (saveToBatchInfo.getUseRoutes().equals("new")) {
                int routeId = saveToBatchInfo.getRouteId();
                if (routeId > 0) {
                    options = "RouteID:" + routeId;
                }
            } else if (saveToBatchInfo.getUseRoutes().equals("default")) {
                int defaultRouteId = saveToBatchInfo.getEcDefaultRoute();
                if (defaultRouteId > 0) {
                    options = "RouteID:" + defaultRouteId;
                }
            } //if saveToBatchInfo.getUseRoutes() equals "current" do nothing.
            
            String useGroups = saveToBatchInfo.getUseGroups();
            if (StringUtils.isNotBlank(useGroups) && useGroups.equals("new")) {
                int groupId = saveToBatchInfo.getGroupId();
                if (groupId > 0) {
                    if (options == null){
                        options = "GroupID:" + groupId;
                    }
                    else {
                        options += ";GroupID:" + groupId;
                    }
                }
            } //if saveToBatchInfo.getUseGroups() equals "current" do nothing.
            
            // Build map of inventoryId to account Id
            List<Integer> inventoryIds = Lists.newArrayList();
            for (InventoryIdentifier identifier : collection) {
                inventoryIds.add(identifier.getInventoryId());
            }
            Map<Integer,Integer> inventoryIdsToAccountIds = customerAccountDao.getAccountIdsByInventoryIds(inventoryIds);
            // Add inventory to the SwitchCommandQueue
            for (InventoryIdentifier invId : hwsToConfig) {
                
                SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
                cmd.setEnergyCompanyID(ecId);
                cmd.setAccountID(inventoryIdsToAccountIds.get(invId.getInventoryId()));
                cmd.setInventoryID(invId.getInventoryId());
                cmd.setCommandType(SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE);
                cmd.setInfoString(options);
                
                commandQueue.addCommand(cmd, false);
                successCount++;
                completedItems++;
            }
            // Write out the file by passing 'true' as the second parameter to addCommand().
            commandQueue.addCommand(null, true);
        }
        
        @Override
        public MessageSourceResolvable getMessage() {
            return new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.config.tasks.current.schedule");
        }
    }
}