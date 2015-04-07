package com.cannontech.capcontrol.service;

import java.util.Collection;
import java.util.List;

import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.RegulatorMappingResult;
import com.cannontech.capcontrol.model.RegulatorMappingTask;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;

/**
 * Handles tasks that automatically set up regulator point mappings to points that have been created with specially
 * formatted names in the form of "RegulatorName-PointMappingString". The "point mapping string" must correspond to the
 * <code>RegulatorPointMapping</code> "mappingString" values.
 */
public interface VoltageRegulatorMappingService {
    
    /**
     * Creates a new task to add point mappings for the specified regulators, and returns the taskId.
     * @return the ID of the newly created task
     */
    public String start(Collection<YukonPao> regulators, YukonUserContext userContext);
    
    /**
     * Retrieves a regulator point mapping task by its ID.
     */
    public RegulatorMappingTask getTask(String taskId);
    
    /**
     * Retrieves a list of all pending and completed regulator point mapping tasks from the cache. (Cache entries do
     * expire, so this is not necessarily a complete list of all tasks ever initiated.)
     */
    public List<RegulatorMappingTask> getAllTasks();
    
    /** Perform a regulator point mapping for a single device */
    public RegulatorMappingResult start(Regulator regulator);
    
}