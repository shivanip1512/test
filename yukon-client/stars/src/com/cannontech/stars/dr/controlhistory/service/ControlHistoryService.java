package com.cannontech.stars.dr.controlhistory.service;

import java.util.List;
import java.util.Map;

import org.joda.time.ReadableDuration;

import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.google.common.collect.ListMultimap;

public interface ControlHistoryService {

    public boolean containsOnlyNotEnrolledHistory(List<ControlHistory> controlHistoryList);
    
    public Map<Integer, ReadableDuration> calculateTotalDuration(ListMultimap<Integer, 
                                                                  ControlHistory> controlHistoryMap);
    
    public ReadableDuration calculateTotalDuration(List<ControlHistory> controlHistoryList);
    
}
