package com.cannontech.common.config;

import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.cannontech.common.util.SimplePeriodFormat;


public interface ConfigurationSource {

    /**
     * Returns value of key from the configuration file. Throws an exception if the key did not
     * exist in the file.
     * @param key
     * @throws UnknownKeyException if key didn't exist
     * @return the value of the key, never null
     */
    public String getRequiredString(String key) throws UnknownKeyException;

    /**
     * Returns value of key from the configuration file. Returns null if
     * the key did not exist in the file.
     * @param key
     * @return the value of the key, or null if the key didn't exist
     */
    public String getString(String key);

    /**
     * Returns value of key from the configuration file. Returns defaultVlaue if
     * the key did not exist in the file.
     * Preferred method is {@link #getString(MasterConfigStringKeysEnum, String)}
     * @param key
     * @param defaultValue string value to return if the key does not exist 
     * @return the value of the key, or defaultValue if the key didn't exist
     */
    public String getString(String key, String defaultValue);

    /**
     * Returns value of key from the configuration file. Returns defaultVlaue if
     * the key did not exist in the file.
     * @param key
     * @param defaultValue String value to return if the key does not exist 
     * @return the value of the key, or defaultValue if the key didn't exist
     */
    public String getString(MasterConfigStringKeysEnum key, String defaultValue);
    
    /**
     * Returns value of key from the configuration file. Throws an exception if the key did not
     * exist in the file.
     * @param key
     * @throws UnknownKeyException if key didn't exist
     * @return the value of the key, never null
     */
    public int getRequiredInteger(String key) throws UnknownKeyException;

    /**
     * Returns value of key from the configuration file. Returns defaultVlaue if
     * the key did not exist in the file.
     * @param key
     * @param defaultValue int value to return if the key does not exist
     * @return the value of the key, or defaultValue if the key didn't exist
     */
    public int getInteger(String key, int defaultValue);
    
    /**
     * Returns value of key from the configuration file. Returns defaultVlaue if
     * the key did not exist in the file.
     * @param key
     * @param defaultValue long value to return if the key does not exist 
     * @return the value of the key, or defaultValue if the key didn't exist
     */
    public long getLong(String key, long defaultValue);
    
    /**
     * Returns value of key from the configuration file. Returns null if
     * the key did not exist in the file.
     * @param key
     * @return the value of the key, or null if the key didn't exist
     */
    public Double getDouble(MasterConfigDoubleKeysEnum key);
    
    /**
     * Returns boolean value of key from the configuration file. Returns a defaultValue if
     * the key did not exist in the file.
     * @param key
     * @param defaultValue boolean value to return if the key does not exist
     * @return the boolean value of the key, or defaultValue if the key didn't exist
     */
    public boolean getBoolean(String key, boolean defaultValue);
    /**
     * Returns boolean value of key from the configuration file. Returns a defaultValue if
     * the key did not exist in the file.
     * @param key
     * @param defaultValue boolean value to return if the key does not exist
     * @return the boolean value of the key, or defaultValue if the key didn't exist
     */
    public boolean getBoolean(MasterConfigBooleanKeysEnum key, boolean defaultValue);

    /**
     * Returns boolean value of key from the configuration file. Returns false if
     * the key did not exist in the file.
     * @param key
     * @return the boolean value of the key, or false if the key didn't exist
     */
    public boolean getBoolean(MasterConfigBooleanKeysEnum key);

    /**
     * <p>Uses the {@link SimplePeriodFormat#getConfigPeriodFormatterWithFallback(DurationFieldType)} style 
     * to convert the string in the configuration
     * file to a period. This format supports simple letter suffixes to indicate the Period.</p>
     * 
     * <p>For example, "1d6h30m" would equal 1 day plus 6 hours plus 30 minutes. The following 
     * letters are supported: </p>
     * <pre>
     *   w -- week
     *   d -- day
     *   h -- hour
     *   m -- minute
     *   s -- seconds
     * </pre>
     * <p>Milliseconds are supported as fractional seconds:</p>
     * <pre>
     *   .05s = 50 milliseconds
     * </pre>
     *   
     * If the string in the configuration file does not contain a letter (e.g. "2000"), the durationFieldType
     * is used to interpret the value. This is useful because various config keys have historically
     * been defined with different units (e.g. one timeout key may be in milliseconds while another
     * is in minutes). This allows this method to be used to read keys that don't use any of the
     * above letter formatting.
     * 
     * @param key
     * @param defaultValue Period value to return if the key does not exist
     * @param durationFieldType field type of unadorned numbers 
     */
    public Period getPeriod(String key, ReadablePeriod defaultValue, DurationFieldType durationFieldType);
    
    /**
     * <p>Uses the {@link SimplePeriodFormat#getConfigPeriodFormatter()} style to convert the string in the configuration
     * file to a period. This format supports simple letter suffixes to indicate the Period.</p>
     * 
     * <p>For example, "1d6h30m" would equal 1 day plus 6 hours plus 30 minutes. The following 
     * letters are supported: </p>
     * <pre>
     *   w -- week
     *   d -- day
     *   h -- hour
     *   m -- minute
     *   s -- seconds
     * </pre>
     * <p>Milliseconds are supported as fractional seconds:</p>
     * <pre>
     *   .05s = 50 milliseconds
     * </pre>
     *   
     * <p>If the string in the configuration file does not contain a letter (e.g. "2000"), an Exception is thrown.</p>
     * 
     * @param key
     * @param defaultValue Period value to return if the key does not exist
     */
    public Period getPeriod(String key, ReadablePeriod defaultValue);
    
    /**
     * Equivalent to calling {@link #getPeriod(String, ReadablePeriod, DurationFieldType)} and then calling
     * {@link Period#toStandardDuration()} on the result.
     * @param key
     * @param defaultValue Duration value to return if the key does not exist
     * @param durationFieldType field type of unadorned numbers 
     */
    public Duration getDuration(String key, ReadableDuration defaultValue, DurationFieldType durationFieldType);
    
    /**
     * Equivalent to calling {@link #getPeriod(String, ReadablePeriod)} and then calling
     * {@link Period#toStandardDuration()} on the result.
     * @param key
     * @param defaultValue Duration value to return if the key does not exist
     */
    public Duration getDuration(String key, ReadableDuration defaultValue);

}