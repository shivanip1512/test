package com.eaton.rest.api.dr;

import com.eaton.framework.ConfigFileReader;

public class PathParameters {
    
    /**
     * Returns value for the specified key from configuration.properties file.
     * 
     */
    public static String getParam(String pathParam) {
        try {
            ConfigFileReader configFileReader = new ConfigFileReader();
            return configFileReader.getApiParameter(pathParam);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
