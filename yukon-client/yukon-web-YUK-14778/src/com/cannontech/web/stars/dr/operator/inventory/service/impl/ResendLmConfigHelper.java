package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService.Status;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.AssetActionFailure;
import com.cannontech.web.stars.dr.operator.inventory.model.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;


public class ResendLmConfigHelper extends InventoryActionsHelper {
    
    @Autowired private InventoryDao inventoryDao;
    @Autowired private HardwareConfigService hardwareConfigService;
    
    private final Logger log = YukonLogManager.getLogger(ResendLmConfigHelper.class);
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
        private final boolean sendOutOfService;
        
        public ResendLmConfigTask(InventoryCollection collection, YukonUserContext context, boolean forceInService,
                boolean sendOutOfService) {
            this.collection = collection;
            this.userContext = context;
            this.forceInService = forceInService;
            this.sendOutOfService = sendOutOfService;
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
                if (canceled){
                    break;
                }
                
                try {
                    log.debug("Resending hardware config");
                    Status status = hardwareConfigService.config(identifier.getInventoryId(), forceInService, sendOutOfService,
                        userContext.getYukonUser());
                    if(status == Status.SUCCESS){
                        successful.add(identifier);
                        successCount++;
                    }else if(status == Status.UNSUPPORTED){
                        unsupported.add(identifier);
                        unsupportedCount++;
                    }
                    
                } catch (CommandCompletionException e) {
                    log.debug("Failure", e);
                    failed.add(identifier);
                    failedCount++;
                    
                    DisplayableLmHardware lmHardware = inventoryDao.getDisplayableLMHardware(identifier.getInventoryId());
                    YukonMessageSourceResolvable reason = 
                            new YukonMessageSourceResolvable(failureKey, e.getMessage());
                    AssetActionFailure failure = new AssetActionFailure(identifier, lmHardware, reason);
                    failures.add(failure);
                }
                completedItems++;
            }    
        }
        
        @Override
        public MessageSourceResolvable getMessage() {
            return new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.config.tasks.current.send");
        }
    }
}