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
    
    public String formatDate(Date date, LiteYukonUser user, String type){
        final TimeZone zone = yukonUserDao.getUserTimeZone(user);
        
        DateFormat df = getDateFormatter(user, type);
        df.setTimeZone(zone);
        if( date != null) {
            return df.format(date);
        }else{
            return null;
        }
    }
    
    public DateFormat getDateFormatter(LiteYukonUser user, String type){
        if (type.equalsIgnoreCase("time")) {
            return new SimpleDateFormat("HH:mm");
        }

        if (type.equalsIgnoreCase("date")) {
            return new SimpleDateFormat("MM/dd/yyyy");
        }

        if (type.equalsIgnoreCase("both")) {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
        }
        return new SimpleDateFormat();
    }
    
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

}
