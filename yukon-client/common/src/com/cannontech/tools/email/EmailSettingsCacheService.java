package com.cannontech.tools.email;

public interface EmailSettingsCacheService {

    /**
     * Returns the value to which the specified key is mapped from Cache.
     */
    public String getValue(SystemEmailSettingsType key);

    /**
     * Update the specified value with the specified key in Cache.
     */
    public void update(SystemEmailSettingsType key, String value);

    /**
     * Write all the email settings and subscribers email IDs to a file.
     */
    public void writeToFile();

}
