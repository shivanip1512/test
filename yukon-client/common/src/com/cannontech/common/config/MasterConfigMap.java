package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimplePeriodFormat;

public class MasterConfigMap implements ConfigurationSource {
    private Map<String, String> configMap = new HashMap<String, String>();
    private InputStream inputStream;
    private Logger log = YukonLogManager.getLogger(MasterConfigMap.class);

    public MasterConfigMap() {
        super();
    }
    
    public void reset() {
        log.debug("reseting");
        configMap.clear();
    }

    public void setConfigSource(InputStream is) {
        this.inputStream = is;
    }
    
    public void initialize() {
        log.debug("starting initialization");
        Pattern keyCharPattern = Pattern.compile("^\\s*([^:#\\s]+)\\s*:\\s*([^#]+)");
        Pattern extCharPattern = Pattern.compile("[^\\p{Print}\n\t\r]");
        Pattern spacePattern = Pattern.compile("\\p{Zs}");
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        int lineNum = 0;
        
        try {
            while ((line = bufReader.readLine()) != null) { 
                lineNum++;
                Matcher extCharMatcher = extCharPattern.matcher(line);
                
                if (extCharMatcher.find()) {
                    log.warn(" Line " + lineNum + ": Extended characters found: " + line);
                }
                line = spacePattern.matcher(line).replaceAll(" ");                
                Matcher keyMatcher = keyCharPattern.matcher(line);
                if (keyMatcher.find()) {
                    log.debug("Found line match: " + line);
                    String key = keyMatcher.group(1);
                    String value = keyMatcher.group(2).trim();
                    if (configMap.containsKey(key)) {
                        log.warn(" Line " + lineNum + ": Duplicate key found while reading Master Config file: " + key);
                    }
                    log.debug("Storing key='" + key + "', value='" + value + "'");
                    configMap.put(key, value);
                }
            }
            bufReader.close();
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    public String getRequiredString(String key) throws UnknownKeyException {
        if (!configMap.containsKey(key)) {
            throw new UnknownKeyException(key);
        }
        String string = configMap.get(key);
        log.debug("Returning '" + string + "' for '" + key + "'");
        return string;
    }

    public String getString(String key) {
        return getString(key, null);
    }
    
    public String getString(String key, String defaultValue) {
        if (!configMap.containsKey(key)) {
            return defaultValue;
        }
        String string = configMap.get(key);
        log.debug("Returning '" + string + "' for '" + key + "'");
        return string;
    }
    
    @Override
    public String getString(MasterConfigBooleanKeysEnum key, String defaultValue) {
        return getString(key.name(), defaultValue);
    }
    
    @Override
    public int getRequiredInteger(String key) throws UnknownKeyException {
    	if (!configMap.containsKey(key)) {
            throw new UnknownKeyException(key);
        }
    	String string = getString(key);
    	return Integer.parseInt(string);
    }
    
    @Override
    public int getInteger(String key, int defaultValue) {
        String string = getString(key);
        if (string == null) {
            return defaultValue;
        }
        return Integer.parseInt(string);
    }
    
    @Override
    public long getLong(String key, long defaultValue) {
        String string = getString(key);
        if (string == null) {
            return defaultValue;
        }
        return Long.parseLong(string);
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
    	
    	if (!configMap.containsKey(key)) {
            return defaultValue;
        }
    	
    	return Boolean.parseBoolean(configMap.get(key));
    }

    @Override
    public boolean getBoolean(MasterConfigBooleanKeysEnum key, boolean defaultValue) {
        return getBoolean(key.name(), defaultValue);
    }

    @Override
    public boolean getBoolean(MasterConfigBooleanKeysEnum key) {
        return getBoolean(key.name(), false);
    }
    
    @Override
    public Period getPeriod(String key, ReadablePeriod defaultValue) {
        String string = getString(key);
        if (StringUtils.isBlank(string)) {
            return defaultValue.toPeriod();
        }
        Period result = SimplePeriodFormat.getConfigPeriodFormatter().parsePeriod(string);
        return result;
    }
    
    @Override
    public Period getPeriod(String key, ReadablePeriod defaultValue, DurationFieldType duationFieldType) {
        String string = getString(key);
        if (StringUtils.isBlank(string)) {
            return defaultValue.toPeriod();
        }
        Period result = SimplePeriodFormat.getConfigPeriodFormatterWithFallback(duationFieldType).parsePeriod(string);
        return result;
    }
    
    @Override
    public Duration getDuration(String key, ReadableDuration defaultValue) {
        return getPeriod(key, defaultValue.toPeriod()).toStandardDuration();
    }
    
    @Override
    public Duration getDuration(String key, ReadableDuration defaultValue, DurationFieldType duationFieldType) {
        return getPeriod(key, defaultValue.toPeriod(), duationFieldType).toStandardDuration();
    }

}
