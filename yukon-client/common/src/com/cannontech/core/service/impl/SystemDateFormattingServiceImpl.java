package com.cannontech.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

public class SystemDateFormattingServiceImpl implements SystemDateFormattingService {

    @Autowired private YukonSettingsDao yukonSettingsDao;
    
    @Override
    public TimeZone getSystemTimeZone() throws BadConfigurationException {
        
        TimeZone timeZone;
        
        String timeZoneStr = yukonSettingsDao.getSettingStringValue(YukonSetting.SYSTEM_TIMEZONE);

        if (StringUtils.isNotBlank(timeZoneStr)) {   //Get the TimeZone from timeZoneStr
            try {
                timeZone = CtiUtilities.getValidTimeZone(timeZoneStr);
                CTILogger.debug("Configuration Role System TimeZone found: " + timeZone.getDisplayName());
            } catch (BadConfigurationException e) {
                throw new BadConfigurationException (e.getMessage() + ".  Invalid value in ConfigurationRole System TimeZone property");
            }
        } else {    //Default to the system timezone if blank
            timeZone = TimeZone.getDefault();
            CTILogger.debug("TimeZone not defined; System TimeZone will be used: " + timeZone.getDisplayName());
        }
        return timeZone;
    }
    
    @Override
    public SimpleDateFormat getSystemDateFormat(DateFormatEnum dateFormatEnum) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatEnum.getFormatString());
        dateFormat.setTimeZone(getSystemTimeZone());
        return dateFormat;
    }
    
    @Override
    public DateTimeFormatter getCommandTimeFormatter() {
    	return DateTimeFormat.forPattern("HH:mm");
    }

    @Override
    public Calendar getSystemCalendar() {
        return Calendar.getInstance(getSystemTimeZone());
    }
}
