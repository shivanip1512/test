package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DateFormattingServiceImpl implements DateFormattingService {

    private YukonUserDao yukonUserDao;
    
    public String formatDate(Date date, DateFormatEnum type, LiteYukonUser user) throws IllegalArgumentException{
        final TimeZone zone = yukonUserDao.getUserTimeZone(user);
        
        DateFormat df = getDateFormatter(type, user);
        df.setTimeZone(zone);
        if (date != null) {
            return df.format(date);
        } else {
            throw new IllegalArgumentException("Date object is null in DateFormattingServiceImpl.formatDate()");
        }
    }
    
    public DateFormat getDateFormatter(DateFormatEnum type, LiteYukonUser user) throws IllegalArgumentException{

        return new SimpleDateFormat(type.getFormat());

    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}
