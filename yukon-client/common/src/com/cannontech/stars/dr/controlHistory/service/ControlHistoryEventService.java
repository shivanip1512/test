package com.cannontech.stars.dr.controlHistory.service;

import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.user.YukonUserContext;

public interface ControlHistoryEventService {

    List<String> getHistoricalGearNames(int programId, List<DateTime> startDates, List<DateTime> endDates, YukonUserContext userContext);
}
