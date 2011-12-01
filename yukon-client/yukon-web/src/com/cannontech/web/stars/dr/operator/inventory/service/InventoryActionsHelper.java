package com.cannontech.web.stars.dr.operator.inventory.service;

import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.stars.dr.hardware.service.HardwareService;

public class InventoryActionsHelper {

    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    protected HardwareService hardwareService;
    protected Executor executor;
    protected static final Logger log = YukonLogManager.getLogger(InventoryActionsHelper.class);
    
    /**
     * Starts an inventory change device type task and returns the task's 
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
    
    @Resource(name="longRunningExecutor")
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    @Required
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
    @Autowired
    public void setHardwareService(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
    }
    
}