package com.cannontech.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class SimpleTemplateProcessor implements TemplateProcessor {
    private static Pattern pattern = Pattern.compile("\\{([^}|]+)(\\|([^}]+))?\\}");
    private Locale locale = Locale.getDefault();
    private TimeZone timeZone = TimeZone.getDefault();
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.TemplateProcessor#process(java.lang.CharSequence, java.util.Map)
     */
    public String process(CharSequence template, Map<String, ? extends Object> values) {
        Stack<StringBuilder> outputStack = new Stack<StringBuilder>();
        outputStack.push(new StringBuilder());
        int currentPos = 0;
        boolean inEscape = false;
        
        for (; currentPos < template.length(); ++currentPos) {
            char c = template.charAt(currentPos);
            if (inEscape) {
                outputStack.peek().append(c);
                inEscape = false;
                continue;
            }

            if (c == '\\') {
                inEscape = true;
                continue;
            }

            if (c == '{') {
                outputStack.push(new StringBuilder());
            }

            outputStack.peek().append(c);

            if (c == '}') {
                Validate.isTrue(outputStack.size() > 1, "unbalanced braces (too many closing)");
                StringBuilder builder = outputStack.pop();
                CharSequence replacement = processToken(builder.toString(), values);
                outputStack.peek().append(replacement);
            }

        }

        StringBuilder result = outputStack.pop();
        Validate.isTrue(outputStack.empty(), "unbalanced braces (too many opening)");
        
        return result.toString();
    }
    
    private CharSequence processToken(CharSequence token, Map<String, ? extends Object> values) {
        Matcher matcher = pattern.matcher(token);
        CharSequence result = token;
        if (matcher.matches()) {
            String key = matcher.group(1);
            Object value = values.get(key);
            if (value != null) {
                String extra = matcher.group(3);
                if (StringUtils.isNotBlank(extra)) {
                    if (value instanceof Number) {
                        NumberFormat format = new DecimalFormat(extra);
                        result = format.format(value);
                    } else if (value instanceof Date) {
                        DateFormat format = new SimpleDateFormat(extra);
                        format.setTimeZone(timeZone);
                        result = format.format(value);
                    } else if (value instanceof Calendar) {
                        Calendar valueCal = (Calendar) value;
                        DateFormat format = new SimpleDateFormat(extra);
                        format.setCalendar(valueCal);
                        result = format.format(valueCal.getTime());
                    } else if (value instanceof Boolean) {
                        boolean showFirst = (Boolean) value;
                        // split extra on the | character
                        String[] strings = extra.split("\\|", 2);
                        if (showFirst) {
                            result = strings[0];
                        } else {
                            result = strings[1];
                        }
                    } else {
                        result = "???";
                    }
                } else {
                    result = value.toString();
                }
            }
        }

        return result;
    }

    public boolean contains(CharSequence template, String key) {
        String searchKey = "{" + key + "}";
        boolean result = template.toString().contains(searchKey);
        return result;
    }
    
    public void setDefaultLocale(Locale locale) {
        this.locale = locale;
    }
    
    public void setDefaultTimeZone(TimeZone timeZone) {
        this.timeZone  = timeZone;
    }
}
