package com.cannontech.openadr.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.openadr.exception.OpenAdrConfigurationException;

/**
 * This service is used to grab configuration information from global settings and 
 * return it in a useful format. 
 */
public interface OpenAdrConfigurationService {
    
    /** 
     * Gets the reply limit
     * @return the reply limit value from global settings if it exists, a default value otherwise.
     */
    public long getReplyLimit();
    
    /** 
     * Gets the request interval
     * @return the request interval value from global settings if it exists, a default value otherwise.
     */
    public int getRequestInterval();
    
    /**
     * Get the VEN ID.
     * @return the VEN ID from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the VEN ID isn't defined in global settings.
     */
    public String getVenId() throws OpenAdrConfigurationException;
    
    /**
     * Get the VTN ID.
     * @return the VTN ID from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the VTN ID isn't defined in global settings.
     */
    public String getVtnId() throws OpenAdrConfigurationException;
    
    /**
     * Get the VTN URL.
     * @return the VTN URL from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the VTN URL isn't defined in global settings.
     */
    public String getVtnUrl() throws OpenAdrConfigurationException;
    
    /**
     * Get the Keystore Path.
     * @return the Keystore Path from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the Keystore Path isn't defined in global settings.
     */
    public String getKeystorePath() throws OpenAdrConfigurationException;
    
    /**
     * Get the Keystore Password.
     * @return the Keystore Password from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the Keystore Password isn't defined in global settings.
     */
    public String getKeystorePassword() throws OpenAdrConfigurationException;
    
    /**
     * Get the Truststore Path.
     * @return the Truststore Path from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the Truststore Path isn't defined in global settings.
     */
    public String getTruststorePath() throws OpenAdrConfigurationException;
    
    /**
     * Get the Truststore Password.
     * @return the Truststore Password from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the Truststore Password isn't defined in global settings.
     */
    public String getTruststorePassword() throws OpenAdrConfigurationException;
    
    /**
     * Get the VTN Thumbprint.
     * @return the VTN Thumbprint from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the VTN Thumbprint isn't defined in global settings.
     */
    public String getVtnThumbprint() throws OpenAdrConfigurationException;
    
    /**
     * Get the OpenADR Yukon User.
     * @return the OpenADR Yukon User from global settings if it exists, throws otherwise
     * @throws OpenAdrConfigurationException if the OpenADR Yukon User isn't defined in global settings.
     */
    public LiteYukonUser getOadrUser() throws OpenAdrConfigurationException;
}
