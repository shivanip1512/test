package com.cannontech.core.service;

import java.text.DateFormat;
import java.util.Date;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface DateFormattingService {
    
    static public enum DateFormatEnum {
        TIME("HH:mm"), 
        DATE("MM/dd/yyyy"), 
        BOTH("MM/dd/yyyy HH:mm:ss z");

        private final String format;

        private DateFormatEnum(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }
    
//    public String formatDate(Date date, String type, LiteYukonUser user);
    public String formatDate(Date date, DateFormatEnum type, LiteYukonUser user);
    
    public DateFormat getDateFormatter(DateFormatEnum type, LiteYukonUser user);
//    public DateFormat getDateFormatter(String type, LiteYukonUser user);
}
