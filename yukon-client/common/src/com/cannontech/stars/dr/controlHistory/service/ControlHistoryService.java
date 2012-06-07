package com.cannontech.stars.dr.controlHistory.service;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.google.common.collect.ListMultimap;

public interface ControlHistoryService {

    public boolean containsOnlyNotEnrolledHistory(List<ControlHistory> controlHistoryList);
    
    public Map<Integer, Duration> calculateTotalDuration(ListMultimap<Integer, 
                                                                  ControlHistory> controlHistoryMap);
    
    public Duration calculateTotalDuration(List<ControlHistory> controlHistoryList);
    
}
