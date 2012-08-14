package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimplePeriodFormat;
import com.cannontech.encryption.CryptoAuthenticationException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.PasswordBasedCryptoException;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class MasterConfigMap implements ConfigurationSource {
    private Map<String, String> configMap = new HashMap<String, String>();
    private InputStream inputStream;
    private Logger log = YukonLogManager.getLogger(MasterConfigMap.class);
    private static final String encryptionIndicator = "(AUTO_ENCRYPTED)";

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

    public void initialize() throws IOException {
        File tmp = File.createTempFile("master", "cfgtmp");
        log.debug("starting initialization");
        Pattern keyCharPattern = Pattern.compile("^\\s*([^:#\\s]+)\\s*:\\s*([^#]+)\\s*(#.*)*");
        Pattern extCharPattern = Pattern.compile("[^\\p{Print}\n\t\r]");
        Pattern spacePattern = Pattern.compile("\\p{Zs}");

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedWriter bufWriter = new BufferedWriter(new FileWriter(tmp));
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
                    String comment = (keyMatcher.group(3) != null) ? keyMatcher.group(3) : ""; // avoid null
                    if (configMap.containsKey(key)) {
                        log.warn(" Line " + lineNum + ": Duplicate key found while reading Master Config file: " + key);
                    }
                    if (MasterConfigEncryptedKeys.isEncryptable(key)) {
                        String encValue = handleEncryptedKey(key,value);
                        bufWriter.write(key + " : " + encValue + " " + comment + "\n");
                    } else {
                        configMap.put(key, value);
                        bufWriter.write(line + "\n");
                    }
                } else {
                    bufWriter.write(line + "\n");
                }
            }
            bufWriter.close();
            bufReader.close();
            if (!FileUtils.contentEquals(tmp, MasterConfigHelper.getMasterCfgLocation())){
                FileUtils.copyFile(tmp, MasterConfigHelper.getMasterCfgLocation());
            }
        } catch (IOException e) {
            log.error(e);
        } catch (CryptoAuthenticationException e) {
            log.error(e);
        }
    }

    private String handleEncryptedKey(String keyStr, String value) throws CryptoAuthenticationException {
        String encryptedValue = null;
        String plainTextValue = null;

        value = StringUtils.deleteWhitespace(value);

        try {
            char[] password = CryptoUtils.getMasterCfgPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);

            if (value.length() > encryptionIndicator.length() && value.substring(0, encryptionIndicator.length()).equals(encryptionIndicator)) {
                // value is encrypted
                plainTextValue = new String(encrypter.decrypt(Hex.decodeHex(value.substring(encryptionIndicator.length()).toCharArray())));
                encryptedValue = value;
            } else {
                // value is plain text
                plainTextValue = value;
                encryptedValue = encryptionIndicator + new String(Hex.encodeHex(encrypter.encrypt(value.getBytes())));
            }
            configMap.put(keyStr, plainTextValue);

        } catch (PasswordBasedCryptoException e) {
            log.warn("caught exception in handleEncryptedKey", e);
            configMap.put(keyStr, value);
            return value;
        } catch (Exception e) {
            log.warn("caught exception in handleEncryptedKey", e);
            configMap.put(keyStr, value);
            return value;        
        } 

        return encryptedValue;
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
    public String getString(MasterConfigStringKeysEnum key, String defaultValue) {
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
