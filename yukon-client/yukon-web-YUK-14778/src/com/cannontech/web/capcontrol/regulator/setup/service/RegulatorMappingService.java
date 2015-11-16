package com.cannontech.web.capcontrol.regulator.setup.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingTask;

/**
 * Handles tasks that automatically set up regulator point mappings to points that have been created with specially
 * formatted names in the form of "RegulatorName-PointMappingString". The "point mapping string" must correspond to the
 * <code>RegulatorPointMapping</code> "mappingString" values.
 */
public interface RegulatorMappingService {
    
    /**
     * Creates a new task to add point mappings for the specified regulators, and returns the taskId.
     * @return the ID of the newly created task
     */
    String start(Collection<YukonPao> regulators, YukonUserContext userContext);
    
    /**
     * Retrieves a regulator point mapping task by its ID.
     */
    RegulatorMappingTask getTask(String taskId);
    
    /**
     * Retrieves a list of all pending and completed regulator point mapping tasks from the cache. (Cache entries do
     * expire, so this is not necessarily a complete list of all tasks ever initiated.)
     */
    List<RegulatorMappingTask> getAllTasks();
    
    /** Perform a regulator point mapping for a single device */
    RegulatorMappingResult start(Regulator regulator);
    
    /** Delete a task. Task will first be canceled if needed. */
    void delete(String taskId);
    
    /** Convert a result to it's JSON representation. */
    Map<String, Object> buildJsonResult(RegulatorMappingResult result, YukonUserContext userContext);
    
}