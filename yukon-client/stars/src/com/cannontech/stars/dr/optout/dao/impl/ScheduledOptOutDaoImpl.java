package com.cannontech.stars.dr.optout.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.dao.ScheduledOptOutDao;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.stars.dr.optout.util.OptOutUtil;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.user.YukonUserContext;

public class ScheduledOptOutDaoImpl implements ScheduledOptOutDao {
    private ECMappingDao ecMappingDao;
    private DateFormattingService dateFormattingService;

    @Override
    public ScheduledOptOut getLastScheduledOptOut(final CustomerAccount customerAccount,
            final YukonUserContext yukonUserContext) throws NotFoundException {

        final int customerAccountId = customerAccount.getAccountId();
        final Date now = dateFormattingService.getCalendar(yukonUserContext).getTime();

        StarsLMProgramEvent lastEvent = getLastEvent(customerAccountId, yukonUserContext);
        
        boolean isEventScheduled = isEventScheduled(lastEvent, now);
        if (!isEventScheduled) {
            throw new NotFoundException("No ScheduledOptOut for customerAccountId " + customerAccountId);
        }
        
        ScheduledOptOut scheduledOptOut = OptOutUtil.toScheduledOptOut(customerAccountId,
                                                                       lastEvent);
        return scheduledOptOut;
    }

    @Override
    public List<ScheduledOptOut> getAll(final CustomerAccount customerAccount,
            final YukonUserContext yukonUserContext) throws NotFoundException {

        final int customerAccountId = customerAccount.getAccountId();
        final Date now = dateFormattingService.getCalendar(yukonUserContext).getTime();
        
        StarsLMProgramHistory programHistory = getProgramHistory(customerAccountId,
                                                                 yukonUserContext);

        final List<ScheduledOptOut> scheduledOptOutList = 
            new ArrayList<ScheduledOptOut>(programHistory.getStarsLMProgramEventCount());
        
        for (int x = 0; x < programHistory.getStarsLMProgramEventCount(); x++) {
            StarsLMProgramEvent event = programHistory.getStarsLMProgramEvent(x);
            
            boolean isScheduledEvent = isEventScheduled(event, now);
            if (!isScheduledEvent) continue;
            
            ScheduledOptOut scheduledOptOut = OptOutUtil.toScheduledOptOut(customerAccountId,
                                                                           event);
            scheduledOptOutList.add(scheduledOptOut);
        }

        return scheduledOptOutList;
    }

    private StarsLMProgramEvent getLastEvent(int customerAccountId,
            YukonUserContext yukonUserContext) throws NotFoundException  {
        
        StarsLMProgramHistory programHistory = getProgramHistory(customerAccountId,
                                                                 yukonUserContext);
        StarsLMProgramEvent lastEvent = 
            programHistory.getStarsLMProgramEvent(programHistory.getStarsLMProgramEventCount() - 1);
        
        return lastEvent;
    }
    
    private StarsLMProgramHistory getProgramHistory(int customerAccountId,
            YukonUserContext yukonUserContext) throws NotFoundException {

        LiteStarsEnergyCompany energyCompany = 
            ecMappingDao.getCustomerAccountEC(customerAccountId);

        StarsLMProgramHistory programHistory = 
            OptOutUtil.getLMProgramHistory(customerAccountId, energyCompany);

        return programHistory;

    }

    private boolean isEventScheduled(StarsLMProgramEvent event, Date now) {
        boolean result = (event.hasDuration() && event.getEventDateTime().after(now));
        return result;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

}
