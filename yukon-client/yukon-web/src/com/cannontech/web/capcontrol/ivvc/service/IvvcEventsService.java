package com.cannontech.web.capcontrol.ivvc.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.util.TimeRange;
import com.cannontech.user.YukonUserContext;

public interface IvvcEventsService {
    
    List<Map<String,Object>> getEventsForRegulatorIds(Iterable<Integer> regulatorIds, TimeRange range, YukonUserContext userContext);
    
    List<Integer> getRegulatorsForBus(int id);
    
    List<Integer> getRegulatorsForZone(int id);
}