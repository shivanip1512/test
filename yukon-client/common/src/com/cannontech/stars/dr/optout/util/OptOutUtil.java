package com.cannontech.stars.dr.optout.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.optout.model.OptOut;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;

public final class OptOutUtil {

    private OptOutUtil() {
        
    }
    
    public static StarsLMProgramHistory getLMProgramHistory(final int customerAccountId,
            final LiteStarsEnergyCompany energyCompany) throws NotFoundException {
        
        final StarsCustAccountInformationDao starsCustAccountInformationDao = 
            YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
        
        LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getByAccountId(customerAccountId);
        
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
    
    public static List<Integer> parseOptOutPeriodString(String optOutPeriodString) {

        List<Integer> optOutPeriodInts = new ArrayList<Integer>();
        try {
            if (!StringUtils.isBlank(optOutPeriodString)) {
                String[] optOutPeriodStrs = StringUtils.split(optOutPeriodString, ',');
                for (String optOutPeriodStr : optOutPeriodStrs) {
                    optOutPeriodInts.add(Integer.valueOf(optOutPeriodStr.trim()));
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Can't parse OptOutPeriod role property value [" + optOutPeriodString + "]", e);
        }
        
        // default to 1 day, if value not set
        if (optOutPeriodInts.isEmpty()) {
            optOutPeriodInts.add(1);
        }

        return optOutPeriodInts;
    }
    
}
