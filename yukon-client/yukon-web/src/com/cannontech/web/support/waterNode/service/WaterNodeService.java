package com.cannontech.web.support.waterNode.service;

import org.joda.time.Instant;

import java.util.List;

import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.voltageDetails.VoltageDetails;

public interface WaterNodeService {
    
    /**
     * @param intervalStart - the start of the report interval
     * @param intervalEnd - the end of the report interval
     * @return a list of analyzed water nodes with battery category and high sleeping current indicator
     */
    public List<WaterNodeDetails> getAnalyzedNodes(Instant intervalStart, Instant intervalEnd);

    /**
     * @param intervalStart - the start of the report interval
     * @param intervalEnd - the end of the report interval
     * @return a list of voltage detail objects containing all voltage data used to perform analysis
     * for the last generated battery node analysis report
     */
    public List<VoltageDetails> getVoltageDetails(Instant intervalStart, Instant intervalEnd);
    
}
