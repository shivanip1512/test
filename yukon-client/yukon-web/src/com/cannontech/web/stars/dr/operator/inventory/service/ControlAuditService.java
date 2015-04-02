package com.cannontech.web.stars.dr.operator.inventory.service;

import java.util.List;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditTask;

public interface ControlAuditService {
    
    /**
     * Start a task that for each inventory in the inventory collection, record whether it had any shed time
     * over the interval specified.
     */
    String start(AuditSettings settings, InventoryCollection collection, YukonUserContext userContext);

    List<ControlAuditTask> getAllTasks();

    ControlAuditTask getTask(String taskId) throws ResultExpiredException;

}