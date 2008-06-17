package com.cannontech.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.roles.yukon.ConfigurationRole;

public class SystemDateFormattingServiceImpl implements SystemDateFormattingService {

    private RoleDao roleDao;
    
    @Override
    public TimeZone getSystemTimeZone() throws BadConfigurationException {
        
        TimeZone timeZone;
        
        String timeZoneStr = roleDao.getGlobalPropertyValue(ConfigurationRole.SYSTEM_TIMEZONE);

        if (StringUtils.isBlank(timeZoneStr))   //Default to the system timezone if blank
            timeZone = TimeZone.getDefault();
        else {  //Get the TimeZone from timeZoneStr
            timeZone = TimeZone.getTimeZone(timeZoneStr);
            try {
                CtiUtilities.validate(timeZone, timeZoneStr);
            } catch (BadConfigurationException e) {
                throw new BadConfigurationException (e.getMessage() + ".  Invalid value in ConfigurationRole System TimeZone property");
            }
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
    public Calendar getSystemCalendar() {
        return Calendar.getInstance(getSystemTimeZone());
    }
    
    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
}
