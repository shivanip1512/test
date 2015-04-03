package com.cannontech.capcontrol.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Cancelable;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.Failable;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * This object stores the parameters to initate a regulator point mapping task, and the results of that task's execution.
 * 
 * To query the current completion status of a task, use the <code>isComplete</code>, <code>isCancelled</code>, and 
 * <code>isErrorOccurred</code> methods. 
 * 
 * To determine how many point mapping operations were successful, use the <code>getSuccessCount</code>, 
 * <code>getPartialSuccessCount</code>, and<code>getFailedCount</code> methods. "Partially successful" indicates that
 * some points on the device were mapped, and others failed.
 * 
 * Detailed results can be retrieved via the <code>getResults</code> and <code>getResultsByType</code> methods.
 */
public class RegulatorMappingTask implements Cancelable, Comparable<RegulatorMappingTask>, Completable, Failable {
    
    private static final Logger log = YukonLogManager.getLogger(RegulatorMappingTask.class);
    
    private String taskId;
    private Instant start;
    private YukonUserContext userContext;
    private final DeviceCollection regulators;
    
    private Map<YukonPao, RegulatorMappingResult> results = new HashMap<>();
    private Multimap<RegulatorMappingResultType, RegulatorMappingResult> resultsByType = ArrayListMultimap.create();
    private int successCount;
    private int partialSuccessCount;
    private int failedCount;
    private int completedCount;
    private Throwable error;
    private boolean canceled = false;
    
    public RegulatorMappingTask(DeviceCollection regulators, YukonUserContext userContext) {
        this.regulators = regulators;
        this.userContext = userContext;
        start = Instant.now();
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    /**
     * Gets the context of the user who initiated this task.
     */
    public YukonUserContext getUserContext() {
        return userContext;
    }
    
    /**
     * Gets the system time that the task was initiated.
     */
    public Instant getStart() {
        return start;
    }
    
    /**
     * Gets the collection of regulator devices that this task will act on.
     */
    public DeviceCollection getRegulators() {
        return regulators;
    }
    
    /**
     * Adds the result of a mapping attempt for a particular regulator and mapping.
     */
    public void addResult(YukonPao regulator, RegulatorPointMapping mapping, RegulatorPointMappingResult type) {
        if (!results.containsKey(regulator)) {
            log.debug("Adding new result for " + regulator);
            RegulatorMappingResult result = new RegulatorMappingResult(regulator);
            results.put(regulator, result);
        }
        
        log.debug("Adding new mapping result for " + regulator + ", mapping: " + mapping + ", result: " + type);
        results.get(regulator).addPointDetail(mapping, type);
    }
    
    /**
     * Gets the number of completely processed devices where all point mappings completed successfully.
     */
    public int getSuccessCount() {
        return successCount;
    }
    
    /**
     * Gets the number of completely processed devices that had some successful point mappings and some failures.
     */
    public int getPartialSuccessCount() {
        return partialSuccessCount;
    }
    
    /**
     * Gets the number of completely processed devices where all point mappings failed.
     */
    public int getFailedCount() {
        return failedCount;
    }
    
    /**
     * Gets the total number of completely processed devices.
     */
    public int getCompleteCount() {
        return completedCount;
    }
    
    /**
     * Gets all the task results.
     */
    public Collection<RegulatorMappingResult> getResults() {
        return results.values();
    }
    
    /**
     * Gets the task results, mapped by the result type (i.e. successful, partially successful, failed)
     */
    public Multimap<RegulatorMappingResultType, RegulatorMappingResult> getResultsByType() {
        return resultsByType;
    }
    
    /**
     * Marks a regulator as having all point mapping attempts completed.
     * The device is not added to the completed count or the success/partial success/failed counts until this method
     * is called.
     */
    public void deviceComplete(YukonPao regulator) {
        
        RegulatorMappingResult result = results.get(regulator);
        result.complete();
        completedCount++;
        
        RegulatorMappingResultType resultType = result.getType();
        log.debug("Completed " + regulator + ". Result: " + resultType);
        
        resultsByType.put(resultType, result);
        if (resultType == RegulatorMappingResultType.SUCCESSFUL) {
            successCount++;
        } else if (resultType == RegulatorMappingResultType.PARTIALLY_SUCCESSFUL) {
            partialSuccessCount++;
        } else {
            failedCount++;
        }
    }
    
    /**
     * Returns true if the task fully completed processing. This method will not return true if the task ended
     * prematurely due to cancellation or an error occurring. These conditions may be checked via the 
     * <code>isErrorOccurred</code> and <code>isCanceled</code> methods.
     */
    @Override
    public boolean isComplete() {
        return completedCount == regulators.getDeviceCount();
    }
    
    @Override
    public void errorOccurred(Throwable throwable) {
        error = throwable;
    }
    
    /**
     * Returns true if an error occurred during processing.
     */
    @Override
    public boolean isErrorOccurred() {
        return error != null;
    }
    
    @Override
    public Throwable getError() {
        return error;
    }
    
    /**
     * Sets the canceled flag on the task so that the process will know
     * to stop working on this task.
     * @return Returns true when cancel was effective (task was not already complete)
     */
    public boolean cancel() {
        log.debug("Task canceled.");
        canceled = true;
        return !isComplete();
    }
    
    /**
     * Returns true if the task was canceled during processing.
     */
    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public int compareTo(RegulatorMappingTask other) {
        return start.compareTo(other.start);
    }
}
