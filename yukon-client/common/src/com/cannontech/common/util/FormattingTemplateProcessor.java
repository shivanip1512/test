package com.cannontech.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import com.cannontech.user.YukonUserContext;

public class FormattingTemplateProcessor extends SimpleTemplateProcessor {
    private static Map<String, SimpleDateFormat> dateFormatCache = new HashMap<String, SimpleDateFormat>();
    private final YukonUserContext userContext;
    
    public FormattingTemplateProcessor(YukonUserContext userContext) {
        this.userContext = userContext;
    }

    /**
     * Returns a shared date formatter. When using result, one must synchronize
     * on the whole class.
     * @param format
     * @return
     */
    private SimpleDateFormat getDateFormatter(String format) {
        SimpleDateFormat simpleDateFormat = dateFormatCache.get(format);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(format, userContext.getLocale());
            dateFormatCache.put(format, simpleDateFormat);
        }
        return simpleDateFormat;
    }

    protected CharSequence formatValue(Object value, String extra) {
        CharSequence result;
        if (value instanceof Number) {
            NumberFormat format;
            if (NumberUtils.isDigits(extra)) {
                format = NumberFormat.getNumberInstance(userContext.getLocale());
                int decimalDigits = NumberUtils.toInt(extra);
                format.setMinimumFractionDigits(decimalDigits);
                format.setMaximumFractionDigits(decimalDigits);
            } else {
                format = new DecimalFormat(extra);
            }
            result = format.format(value);
        } else if (value instanceof Date) {
            synchronized (FormattingTemplateProcessor.class) {
                DateFormat format = getDateFormatter(extra);
                format.setTimeZone(userContext.getTimeZone());
                result = format.format(value);
            }
        } else if (value instanceof Calendar) {
            synchronized (FormattingTemplateProcessor.class) {
                Calendar valueCal = (Calendar) value;
                DateFormat format = getDateFormatter(extra);
                format.setCalendar(valueCal);
                result = format.format(valueCal.getTime());
            }
        } else {
            result = super.formatValue(value, extra);
        }
        return result;
    }

}
