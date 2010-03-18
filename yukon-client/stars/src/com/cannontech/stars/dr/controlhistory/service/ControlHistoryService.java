package com.cannontech.stars.dr.controlhistory.service;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.google.common.collect.ListMultimap;

public interface ControlHistoryService {

    public boolean containsOnlyNotEnrolledHistory(List<ControlHistory> controlHistoryList);
    
    public Map<Integer, Integer> calculateTotalDuration(ListMultimap<Integer, ControlHistory> controlHistoryMap);
    
    public Integer calculateTotalDuration(List<ControlHistory> controlHistoryList);
    
}
