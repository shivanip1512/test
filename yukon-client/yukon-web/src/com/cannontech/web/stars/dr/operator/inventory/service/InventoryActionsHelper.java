package com.cannontech.web.stars.dr.operator.inventory.service;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;

public class InventoryActionsHelper {
    
    @Autowired @Qualifier("inventoryTasks") protected RecentResultsCache<AbstractInventoryTask> resultsCache;
    @Autowired @Qualifier("longRunning") protected Executor executor;
    @Autowired protected HardwareService hardwareService;
    
    protected static final Logger log = YukonLogManager.getLogger(InventoryActionsHelper.class);
    
    /**
     * Starts an inventory task and returns the task's 
     * recent result cache identifier.
     * @param collection
     * @return taskId
     */
    public <T extends AbstractInventoryTask> String startTask(T task) {
        
        executor.execute(task.getProcessor());
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        return taskId;
    }
    
}