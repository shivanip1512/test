package com.cannontech.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.cannontech.capcontrol.CapControlCbcFormatConverters;
import com.cannontech.clientutils.CTILogger;
import com.google.common.collect.ImmutableMap;

public class SimpleTemplateProcessor {
    private static Pattern templateExtraPattern = Pattern.compile("\\{([^|]+)(\\|(.+))?\\}", Pattern.DOTALL);
    private static Pattern collectionExtraPattern = Pattern.compile("([^|]+)\\|([^|]+)\\|(.+)", Pattern.DOTALL);

    private static Map<String, Function<Double, String>> specialConverters;
    static {
        ImmutableMap.Builder<String, Function<Double, String>> builder = ImmutableMap.builder();

        builder.put("neutralCurrent", new Function<Double, String>() {
            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertNeutralCurrent(value);
            }
        });
        builder.put("ipAddress", new Function<Double, String>() {
            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertToIpAddress(value);
            }
        });
        builder.put("firmwareVersion", new Function<Double, String>() {

            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertToFirmwareVersion(value);
            }
        });
        builder.put("long", new Function<Double, String>() {

            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertLong(value);
            }
        });
        builder.put("lastControlReason", new Function<Double, String>() {

            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertLastControlReason(value);
            }
        });
        builder.put("lastControlReasonColor", new Function<Double, String>() {

            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertLastControlReasonColor(value);
            }
        });
        builder.put("ignoredControlReason", new Function<Double, String>() {
            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertIgnoredControlReason(value);
            }
        });
        builder.put("ignoredControlReasonColor", new Function<Double, String>() {
            @Override
            public String apply(Double value) {
                return CapControlCbcFormatConverters.convertIgnoredControlReasonColor(value);
            }
        });
        specialConverters = builder.build();
    }
    
    public SimpleTemplateProcessor() {
    }

    public String process(Resource template, Map<String, ? extends Object> values) throws IOException {
        InputStream inputStream = template.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String templateStr = FileCopyUtils.copyToString(inputStreamReader);
        return process(templateStr, values);
    }

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
        // This is synchronized because it uses shared SimpleDateFormat objects. My thinking
        // is that synchronizing is faster than constructing these things.
        Matcher matcher = templateExtraPattern.matcher(token);
        CharSequence result = token;
        if (matcher.matches()) {
            String key = matcher.group(1);
            if (values.containsKey(key)) {
                Object value = values.get(key);
                String extra = matcher.group(3);
                if (StringUtils.isNotBlank(extra)) {
                    if (specialConverters.containsKey(extra)) {
                        Function<Double, String> converter = specialConverters.get(extra);
                        result = converter.apply((Double) value);
                    } else {
                        result = formatValue(value, extra);
                        if (result == null) {
                            result = "???Unknown data type???";
                            if (value != null) {
                                CTILogger.debug("Unknown data type: " + value.getClass());
                            }
                            else {
                                CTILogger.debug("Unknown data type: null value");
                            }
                        }
                    }
                } else {
                    result = value == null ? "" : value.toString();
                }
            }
        }
        return result;
    }

    protected CharSequence handleIterableToken(Iterable<?> iterableValue, String extra) {
        CharSequence result;
        // split extra on the | character
        String[] strings = extra.split("\\|");
        if (strings.length == 1) {
            result = StringUtils.join(iterableValue.iterator(), extra);
        } else {
            Matcher extraMatcher = collectionExtraPattern.matcher(extra);
            if (extraMatcher.matches()) {
                String separator = extraMatcher.group(1);
                String variableName = extraMatcher.group(2);
                String nestedTemplate = extraMatcher.group(3);
                StringBuilder resultBuilder = new StringBuilder();
                boolean first = true;
                Iterator<?> iterator = iterableValue.iterator();
                while (iterator.hasNext()) {
                    if (!first) {
                        resultBuilder.append(separator);
                    }
                    first = false;
                    Object thisValue = iterator.next();
                    Map<String, Object> itMap = Collections.singletonMap(variableName, thisValue);
                    CharSequence subValue = process(nestedTemplate, itMap);
                    resultBuilder.append(subValue);
                }
                result = resultBuilder.toString();
            } else {
                result = "???Bad Iterable Format???";
                CTILogger.debug("Unable to parse extra string for iterable: " + extra);
            }
        }
        return result;
    }
    
    final protected CharSequence formatValue(Object value, String extra) {
        try {
            // see if custom format method exists
            // split extra on last "."
            int endIndex = extra.lastIndexOf(".");
            if (endIndex > 0) {
                String className = extra.substring(0, endIndex);
                Class<?> theClassPart = Class.forName(className);
                String methodName = extra.substring(endIndex+1);
                Method method = theClassPart.getMethod(methodName, value.getClass());
                Object formattedOuput = method.invoke(null, value);
                CharSequence result = formattedOuput.toString();

                return result;
            }
        } catch (Exception e) {
            //Not a valid method name, fall through to type-based formatters
        }
        
        CharSequence result = formatByType(value, extra);
        return result;
    }

    protected CharSequence formatByType(Object value, String extra) {
        CharSequence result;
        if (value instanceof Boolean) {
            boolean showFirst = (Boolean) value;
            // split extra on the | character
            String[] strings = extra.split("\\|", 2);
            if (showFirst) {
                result = strings[0];
            } else {
                result = strings[1];
            }
        } else if (value instanceof Iterable) {
            Iterable<?> iterableValue = (Iterable<?>) value;
            result = handleIterableToken(iterableValue, extra);
        } else {
            result = null;
        }
		return result;
	}


    public boolean contains(CharSequence template, String key) {
        String searchKey = "{" + key; // not perfect
        boolean result = template.toString().contains(searchKey);
        return result;
    }

}
