package com.cannontech.web.support.waterNode.service;

import org.joda.time.Instant;

import java.io.File;
import java.util.List;

import com.cannontech.web.support.waterNode.details.WaterNodeDetails;

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
     * @param file - the uploaded csv file
     * @return
     */
    public List<WaterNodeDetails> getCsvAnalyzedNodes(Instant intervalStart, Instant intervalEnd, File file);
    /**
     * @param intervalStart - the start of the report interval
     * @param intervalEnd - the end of the report interval
     * @return a list of WaterNodeDetails objects containing all voltage data used to perform analysis
     * for the last generated battery node analysis report
     */
    public List<WaterNodeDetails> getVoltageDetails(Instant intervalStart, Instant intervalEnd);
    
}
