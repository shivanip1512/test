package com.cannontech.common.config;


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
     * Returns value of key from the configuration file. Returns a blank string if
     * the key did not exist in the file.
     * @param key
     * @return the value of the key, or null if the key didn't exist
     */
    public String getString(String key);

    /**
     * Returns value of key from the configuration file. Returns a blank string if
     * the key did not exist in the file.
     * @param key
     * @return the value of the key, or defaultValue if the key didn't exist
     */
    public String getString(String key, String defaultValue);
    
    public int getRequiredInteger(String key) throws UnknownKeyException;
    
    public int getInteger(String key, int defaultValue);
    public long getLong(String key, long defaultValue);
    
    /**
     * Returns boolean value of key from the configuration file. Returns a defaultValue if
     * the key did not exist in the file.
     * @param key
     * @param defaultValue boolean value to return if the key does not exist
     * @return the boolean value of the key, or defaultValue if the key didn't exist
     */
    public boolean getBoolean(String key, boolean defaultValue);
    
}