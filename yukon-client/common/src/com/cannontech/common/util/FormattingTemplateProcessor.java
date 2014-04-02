package com.cannontech.common.util;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;

public class FormattingTemplateProcessor extends SimpleTemplateProcessor {
    private Map<String, DateTimeFormatter> dateFormatCache = new HashMap<String, DateTimeFormatter>();
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
    private DateTimeFormatter getDateFormatter(String format) {
        DateTimeFormatter dateFormat = dateFormatCache.get(format);
        if (dateFormat == null) {
            try {
                DateFormatEnum formatEnum = DateFormatEnum.valueOf(format);
                dateFormat = dateFormattingService.getDateTimeFormatter(formatEnum, userContext);
            } catch (IllegalArgumentException e) {
                dateFormat = DateTimeFormat.forPattern(format).withLocale(userContext.getLocale()).withZone(userContext.getJodaTimeZone());
            }
            dateFormatCache.put(format, dateFormat);
        }
        return dateFormat;
    }

    @Override
    protected CharSequence formatByType(Object value, String extra) {
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
            DateTimeFormatter format = getDateFormatter(extra);
            result = format.print(new Instant(value));
        } else if (value instanceof ReadableInstant) {
            DateTimeFormatter format = getDateFormatter(extra);
            result = format.print((ReadableInstant) value);
        } else if (value instanceof Color) {
            Color color = (Color) value;
            result = String.format(extra, color.getRed(), color.getGreen(), color.getBlue());
        } else {
            result = super.formatByType(value, extra);
        }
        return result;
    }

}
