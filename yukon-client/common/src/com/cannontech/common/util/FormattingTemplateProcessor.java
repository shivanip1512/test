package com.cannontech.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;

public class FormattingTemplateProcessor extends SimpleTemplateProcessor {
    private Map<String, DateFormat> dateFormatCache = new HashMap<String, DateFormat>();
    private final YukonUserContext userContext;
    private final DateFormattingService dateFormattingService;
    
    public FormattingTemplateProcessor(DateFormattingService dateFormattingService, YukonUserContext userContext) {
        this.dateFormattingService = dateFormattingService;
        this.userContext = userContext;
    }

    /**
     * Returns a shared date formatter. When using result, one must synchronize
     * on the whole class.
     * @param format
     * @return
     */
    private DateFormat getDateFormatter(String format) {
        DateFormat dateFormat = dateFormatCache.get(format);
        if (dateFormat == null) {
            try {
                DateFormatEnum formatEnum = DateFormatEnum.valueOf(format);
                dateFormat = dateFormattingService.getDateFormatter(formatEnum, userContext);
            } catch (IllegalArgumentException e) {
                dateFormat = new SimpleDateFormat(format, userContext.getLocale());
                dateFormat.setTimeZone(userContext.getTimeZone());
            }
            dateFormatCache.put(format, dateFormat);
        }
        return dateFormat;
    }

    protected CharSequence formatValue(Object value, String extra) {
        CharSequence result;
        if (value instanceof Number) {
            NumberFormat format;
            if (NumberUtils.isDigits(extra)) {
                // I'm not sure that this is a good idea, but other 
                // than a single '0', no digits make sense as a format string.
                format = NumberFormat.getNumberInstance(userContext.getLocale());
                int decimalDigits = NumberUtils.toInt(extra);
                format.setMinimumFractionDigits(decimalDigits);
                format.setMaximumFractionDigits(decimalDigits);
            } else {
                format = new DecimalFormat(extra);
            }
            result = format.format(value);
        } else if (value instanceof Date) {
            synchronized (this) {
                DateFormat format = getDateFormatter(extra);
                result = format.format(value);
            }
        } else {
            result = super.formatValue(value, extra);
        }
        return result;
    }

}
