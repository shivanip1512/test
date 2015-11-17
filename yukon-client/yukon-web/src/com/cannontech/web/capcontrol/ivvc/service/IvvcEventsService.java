package com.cannontech.web.capcontrol.ivvc.service;

import java.util.List;

import com.cannontech.common.util.TimeRange;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.service.impl.IvvcEventsServiceImpl.IvvcEvent;

public interface IvvcEventsService {
    
    List<IvvcEvent> getCapBankEvents(List<Integer> zoneIds, TimeRange range);

    List<IvvcEvent> getCommStatusEvents(int subBusId, TimeRange range);

    List<IvvcEvent> getRegulatorEvents(Iterable<Integer> regulatorIds, TimeRange range, YukonUserContext context);
}