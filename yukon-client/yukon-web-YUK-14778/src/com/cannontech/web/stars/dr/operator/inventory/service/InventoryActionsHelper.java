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
    
}