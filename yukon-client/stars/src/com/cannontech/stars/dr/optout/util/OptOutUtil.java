package com.cannontech.stars.dr.optout.util;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.dr.optout.model.OptOut;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;

public final class OptOutUtil {

    private OptOutUtil() {
        
    }
    
    public static StarsLMProgramHistory getLMProgramHistory(final int customerAccountId,
            final LiteStarsEnergyCompany energyCompany) throws NotFoundException {
        
        LiteStarsCustAccountInformation liteAcctInfo = 
            energyCompany.getCustAccountInformation(customerAccountId, true);
        
        StarsLMProgramHistory programHistory = 
            StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany);
        
        if ((programHistory == null) || (programHistory.getStarsLMProgramEventCount() == 0)) { 
            throw new NotFoundException("No events found for customerAccountId: " + customerAccountId);
        }
        
        return programHistory;
    }
    
    public static OptOut toOptOut(final int customerAccountId, 
            final StarsLMProgramEvent event) {
        
        int durationInHours = event.getDuration();
        Date startDate = event.getEventDateTime();
        Date endDate = (durationInHours > 24) ?
            DateUtils.addHours(startDate, durationInHours) : null;
        
        OptOut optOut = new OptOut();
        optOut.setCustomerAccountId(customerAccountId);
        optOut.setStartDate(startDate);
        optOut.setEndDate(endDate);
        optOut.setDurationInHours(durationInHours);
        return optOut;
    }
    
    public static ScheduledOptOut toScheduledOptOut(final int customerAccountId,
            final StarsLMProgramEvent event) {
        
        int durationInHours = event.getDuration();
        Date startDate = event.getEventDateTime();
        Date endDate = (durationInHours > 24) ?
            DateUtils.addHours(startDate, durationInHours) : null;
        
        ScheduledOptOut scheduledOptOut = new ScheduledOptOut();
        scheduledOptOut.setCustomerAccountId(customerAccountId);
        scheduledOptOut.setStartDate(startDate);
        scheduledOptOut.setEndDate(endDate);
        scheduledOptOut.setDurationInHours(durationInHours);
        return scheduledOptOut;
    }
    
}
