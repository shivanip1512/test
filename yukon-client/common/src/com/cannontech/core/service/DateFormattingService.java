package com.cannontech.core.service;

import java.text.DateFormat;
import java.util.Date;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface DateFormattingService {
    public String formatDate(Date date, LiteYukonUser user, String type);
    public DateFormat getDateFormatter(LiteYukonUser user, String type);
}
