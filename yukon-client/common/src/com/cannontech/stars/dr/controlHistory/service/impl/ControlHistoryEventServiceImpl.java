package com.cannontech.stars.dr.controlHistory.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.controlHistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlHistory.service.ControlHistoryEventService;
import com.cannontech.user.YukonUserContext;

public class ControlHistoryEventServiceImpl implements ControlHistoryEventService {
    @Autowired private ControlHistoryEventDao controlHistoryEventDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public List<String> getHistoricalGearNames(int programId, List<DateTime> startDates, List<DateTime> endDates,
            YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String na = accessor.getMessage("yukon.common.na");
        List<String> gearNames = new ArrayList<>();
        
        DateTime endDate = null;
        Iterator<DateTime> endDatesIterator = endDates.iterator();
        for (DateTime startDate : startDates) {
            endDate = endDatesIterator.next();
            gearNames.add(controlHistoryEventDao.getHistoricalGearName(programId, startDate, endDate, na));
        }
        return gearNames;
    }

}
