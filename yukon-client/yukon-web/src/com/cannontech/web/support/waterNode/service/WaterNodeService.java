package com.cannontech.web.support.waterNode.service;

import org.joda.time.Instant;

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
     * @return a list of rows found in the detailed voltage data report
     */
    public List<String[]> getDetailedReportRows(Instant intervalStart, Instant intervalEnd);
    
}
