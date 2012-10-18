package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimplePeriodFormat;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.MasterConfigCryptoUtils;
import com.google.common.base.Charsets;

public class MasterConfigMap implements ConfigurationSource {
    private Map<String, String> configMap = new HashMap<String, String>();
    private File masterCfgFile;
    private Logger log = YukonLogManager.getLogger(MasterConfigMap.class);

    public MasterConfigMap(File file) throws IOException, CryptoException {
        super();
        setConfigSource(file);
        initialize();
    }

    public void reset() {
        LogHelper.debug(log, "resetting");
        configMap.clear();
    }

    public void setConfigSource(File file) {
        this.masterCfgFile = file;
    }

    public void initialize() throws IOException, CryptoException {
        configMap.clear();
        boolean updateFile = false;
        String endl = System.getProperty("line.separator");
        LogHelper.debug(log, "starting initialization");
        Pattern keyCharPattern = Pattern.compile("^\\s*([^:#\\s]+)\\s*:\\s*([^#]+)\\s*(#.*)*");
        Pattern extCharPattern = Pattern.compile("[^\\p{Print}\n\t\r]");
        Pattern spacePattern = Pattern.compile("\\p{Zs}");
        
        InputStream inputStream = FileUtils.openInputStream(masterCfgFile);
        BufferedReader masterCfgReader = new BufferedReader(new InputStreamReader(inputStream));
        // As we are parsing master.cfg, we copy it into this temporary string.  If we come across
        // value which needs to be encrypted, we encrypt it and set updateFile to true.  Then, if
        // updateFile is true, we overwrite the master.cfg file with the value of this string.
        // (If updateFile is false, we know that nothing needed to be encrypted so we can leave
        // the file alone.)
        StringBuilder tempWriter = new StringBuilder();
        String line = null;
        int lineNum = 0;

        while ((line = masterCfgReader.readLine()) != null) { 
            lineNum++;
            Matcher extCharMatcher = extCharPattern.matcher(line);

            if (extCharMatcher.find()) {
                LogHelper.warn(log, " Line %d: Extended characters found: %s", lineNum, line);
            }
            line = spacePattern.matcher(line).replaceAll(" ");
            Matcher keyMatcher = keyCharPattern.matcher(line);
            if (keyMatcher.find()) {
                LogHelper.debug(log, "Found line match: %s", line);
                String key = keyMatcher.group(1);
                String value = keyMatcher.group(2).trim();
                String comment = (keyMatcher.group(3) != null) ? keyMatcher.group(3) : ""; // avoid null
                if (configMap.containsKey(key)) {
                    LogHelper.warn(log, "Line %d: Duplicate key found while reading Master Config file: %s", lineNum, key);
                }
                if (MasterConfigCryptoUtils.isSensitiveData(key)) {
                    if (MasterConfigCryptoUtils.isEncrypted(value)) {
                        // Found a value already encrypted
                        String valueDecrypted = MasterConfigCryptoUtils.decryptValue(value);
                        tempWriter.append(line);
                        configMap.put(key, valueDecrypted);
                    } else {
                        updateFile = true;
                        // Found a value that needs to be encrypted
                        String valueEncrypted = MasterConfigCryptoUtils.encryptValue(value);
                        tempWriter.append(key).append(" : ").append(valueEncrypted).append(" ").append(comment);
                        configMap.put(key, value);
                    }
                } else {
                    // Non-sensitive data.
                    configMap.put(key, value);
                    tempWriter.append(line);
                }
            } else {
                // Line with no "key : value" pair
                tempWriter.append(line);
            }
            tempWriter.append(endl);
        }
        masterCfgReader.close();
        if (updateFile) {
            FileUtils.writeStringToFile(masterCfgFile, tempWriter.toString(), Charsets.UTF_8.name());
        }
    }

    @Override
    public String getRequiredString(MasterConfigStringKeysEnum key) throws UnknownKeyException {
        return getRequiredString(key.name());
    }
    
    @Override
    public String getRequiredString(String key) throws UnknownKeyException {
        if (!configMap.containsKey(key)) {
            throw new UnknownKeyException(key);
        }
        String string = configMap.get(key);
        LogHelper.debug(log, "Returning '%s' for '%s'", string, key);
        return string;
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        if (!configMap.containsKey(key)) {
            return defaultValue;
        }
        String string = configMap.get(key);
        LogHelper.debug(log, "Returning '%s' for '%s'", string, key);
        return string;
    }

    @Override
    public String getString(MasterConfigStringKeysEnum key, String defaultValue) {
        return getString(key.name(), defaultValue);
    }

    @Override
    public int getRequiredInteger(String key) throws UnknownKeyException {
        String string = getRequiredString(key);
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
    public Double getDouble(MasterConfigDoubleKeysEnum key) {
        String string = getString(key.toString());
        if (string == null) {
            return null;
        }
        return Double.parseDouble(string);
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
    	String string = getString(key.name());
    	if (string == null) {
    	    return defaultValue;
    	}
    	return Boolean.parseBoolean(string);
    }

    @Override
    public boolean getBoolean(MasterConfigBooleanKeysEnum key) {
        return getBoolean(key, false);
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
