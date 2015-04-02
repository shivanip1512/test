package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.AssetActionFailure;
import com.cannontech.web.stars.dr.operator.inventory.model.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class ResendLmConfigHelper extends InventoryActionsHelper {
    
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareCommandService commandService;
    @Autowired private InventoryConfigEventLogService eventLog;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    
    /**
     * Starts an inventory task and returns the task's 
     * recent result cache identifier.
     */
    public String startTask(ResendLmConfigTask task) {
        
        executor.execute(task);
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        return taskId;
    }
    
    public class ResendLmConfigTask extends CollectionBasedInventoryTask implements Runnable {
        
        private static final String failureKey = "yukon.web.modules.operator.inventory.config.send.failureMessage";
        
        private Set<InventoryIdentifier> unsupported = new HashSet<>();
        private Set<InventoryIdentifier> successful = new HashSet<>();
        private Set<InventoryIdentifier> failed = new HashSet<>();
        private Set<AssetActionFailure> failures = new HashSet<>();
        
        private final boolean forceInService;
        
        public ResendLmConfigTask(InventoryCollection collection, YukonUserContext context,boolean forceInService) {
            this.collection = collection;
            this.userContext = context;
            this.forceInService = forceInService;
        }
        
        public Set<InventoryIdentifier> getSuccessful() {
            return successful;
        }
        
        public Set<InventoryIdentifier> getFailed() {
            return failed;
        }
        
        public Set<AssetActionFailure> getFailures() {
            return failures;
        }
        
        public Set<InventoryIdentifier> getUnsupported() {
            return unsupported;
        }
        
        @Override
        public void run() {
            for (InventoryIdentifier identifier : collection.getList()) {
                
                if (canceled) break;
                
                int inventoryId = identifier.getInventoryId();
                LiteLmHardwareBase lmhb = null;
                
                try {
                    lmhb = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
                } catch (NotFoundException e) {
                    // handled below
                }
                
                LiteYukonUser user = userContext.getYukonUser();
                
                if (lmhb != null && identifier.getHardwareType().isConfigurable()) {
                    
                    String sn = lmhb.getManufacturerSerialNumber();
                    
                    try {
                        
                        LmHardwareCommand command = new LmHardwareCommand();
                        command.setDevice(lmhb);
                        command.setType(LmHardwareCommandType.CONFIG);
                        command.setUser(user);
                        command.getParams().put(LmHardwareCommandParam.BULK, true);
                        
                        if (forceInService) {
                            command.getParams().put(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
                        }
                        
                        commandService.sendConfigCommand(command);
                        
                        successful.add(identifier);
                        successCount++;
                        
                        eventLog.itemConfigSucceeded(user, sn);
                    
                    } catch (CommandCompletionException e) {
                        
                        failed.add(identifier);
                        failedCount++;
                        
                        DisplayableLmHardware lmHardware = inventoryDao.getDisplayableLMHardware(inventoryId);
                        YukonMessageSourceResolvable reason = 
                                new YukonMessageSourceResolvable(failureKey, e.getMessage());
                        AssetActionFailure failure = new AssetActionFailure(identifier, lmHardware, reason);
                        failures.add(failure);
                        
                        eventLog.itemConfigFailed(user, sn, e.getMessage());
                        
                    } finally {
                        completedItems++;
                    }
                    
                } else {
                    
                    unsupported.add(identifier);
                    unsupportedCount++;
                    completedItems++;
                    
                    String sn = lmhb != null ? lmhb.getManufacturerSerialNumber() : "";
                    eventLog.itemConfigUnsupported(user, sn);
                }
            }
        }
        
        @Override
        public MessageSourceResolvable getMessage() {
            return new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.config.tasks.current.send");
        }
    }

}