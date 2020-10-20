package com.cannontech.tools.email;

import java.util.Map;

import com.cannontech.tools.smtp.SmtpMetadataConstants;

public interface EmailSettingsCacheService {

    /**
     * Returns the value to which the specified key is mapped from Cache.
     */
    public String getValue(SmtpMetadataConstants key);

    /**
     * Update the specified value with the specified key in Cache.
     */
    public void update(SmtpMetadataConstants key, String value);

    /**
     * Write all the email settings and subscribers email IDs to a file.
     */
    public void writeToFile();

    /**
     * Return a Map after decrypting key and value from the file.
     */
    public Map<SmtpMetadataConstants, String> readFromFile();

}
