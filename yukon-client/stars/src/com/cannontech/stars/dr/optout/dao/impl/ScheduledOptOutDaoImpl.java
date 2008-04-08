package com.cannontech.stars.dr.optout.dao.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.dao.ScheduledOptOutDao;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.user.YukonUserContext;

public class ScheduledOptOutDaoImpl implements ScheduledOptOutDao {
    private ECMappingDao ecMappingDao;
    
    @SuppressWarnings("null")
    @Override
    public ScheduledOptOut getByCustomerAccount(final CustomerAccount customerAccount, 
            final YukonUserContext yukonUserContext) throws NotFoundException {
        
        final int customerAccountId = customerAccount.getAccountId();
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
        LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(customerAccountId, true);
        
        StarsLMProgramHistory programHistory = StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany);
        
        if ((programHistory == null) || (programHistory.getStarsLMProgramEventCount() == 0)) { 
            throwNotFoundException(customerAccountId);
        }    
        
        StarsLMProgramEvent lastEvent = programHistory.getStarsLMProgramEvent(programHistory.getStarsLMProgramEventCount() - 1);
        
        final Date now = new Date();
        
        if (!(lastEvent.hasDuration() && lastEvent.getEventDateTime().after(now))) {
            throwNotFoundException(customerAccountId);
        }    
        
        final Date startDate = lastEvent.getEventDateTime();
        Date endDate = null;
        
        if (lastEvent.getDuration() > 24) {
            Calendar cal = Calendar.getInstance(yukonUserContext.getTimeZone());
            cal.setTime(startDate);
            cal.add(Calendar.HOUR_OF_DAY, lastEvent.getDuration());
            endDate = cal.getTime();
        }
        
        final ScheduledOptOut scheduledOptOut = new ScheduledOptOut();
        scheduledOptOut.setCustomerAccountId(customerAccountId);
        scheduledOptOut.setStartDate(startDate);
        scheduledOptOut.setEndDate(endDate);
        scheduledOptOut.setDuration(lastEvent.getDuration());
        return scheduledOptOut;
    }
    
    private void throwNotFoundException(int customerAccountId) throws NotFoundException {
        throw new NotFoundException("No ScheduledOptOut found for CustomerAccount with id: " + customerAccountId);
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
}
