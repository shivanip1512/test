package com.cannontech.capcontrol.model;

import static com.cannontech.capcontrol.model.RegulatorPointMappingResult.*;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Completable;

/**
 * The point mapping result for a single regulator device.
 * 
 * The <code>getType</code> method returns a {@link RegulatorMappingResultType} that represents the device-level result
 * (i.e. successful, partially successful, failed). The <code>getPointMappingResults</code> method return a map of
 * {@link RegulatorPointMappingResult}, which represents the results of each individual mapping on the device.
 */
public class RegulatorMappingResult implements Completable {
    private final YukonPao regulator;
    private final Map<RegulatorPointMapping, RegulatorPointMappingResult> pointResultMap = new HashMap<>();
    private boolean hasSuccessfulPointResults = false;
    private boolean hasFailedPointResults = false;
    private boolean isComplete = false;
    
    public RegulatorMappingResult(YukonPao regulator) {
        this.regulator = regulator;
    }
    
    public void addPointDetail(RegulatorPointMapping mapping, RegulatorPointMappingResult type) {
        
        //store the result
        pointResultMap.put(mapping, type);
        
        //keep track of overall success/failure for producing the device-level result
        if (type == MULTIPLE_POINTS_FOUND || type == NO_POINTS_FOUND) {
            hasFailedPointResults = true;
        } else if (type == SUCCESS || type == SUCCESS_WITH_OVERWRITE) {
            hasSuccessfulPointResults = true;
        }    
    }
    
    /**
     * Get the detailed per-point-mapping results.
     */
    public Map<RegulatorPointMapping, RegulatorPointMappingResult> getPointMappingResults() {
        return pointResultMap;
    }
    
    /**
     * Get the overall result for the device.
     */
    public RegulatorMappingResultType getType() {
        if (!isComplete) {
            return RegulatorMappingResultType.INCOMPLETE;
        } else if (hasSuccessfulPointResults && !hasFailedPointResults) {
            return RegulatorMappingResultType.SUCCESSFUL;
        } else if (hasSuccessfulPointResults && hasFailedPointResults) {
            return RegulatorMappingResultType.PARTIALLY_SUCCESSFUL;
        } else {
            return RegulatorMappingResultType.FAILED;
        }
    }
    
    public YukonPao getRegulator() {
        return regulator;
    }
    
    public void complete() {
        isComplete = true;
    }
    
    /**
     * Returns true when all point mappings on the device have been fully processed.
     */
    @Override
    public boolean isComplete() {
        return isComplete;
    }
    
}