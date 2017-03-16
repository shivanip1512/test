package com.cannontech.common.config;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationLoaderTest {
    
    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    
    
    @Before
    public void setup(){
        File file = null;
        try {
            file = new File(getClass().getResource("configuration.properties").toURI());
        } catch (URISyntaxException e) {
            file = new File(getClass().getResource("configuration.properties").getPath());
        }
        configurationLoader.setConfigFileLocation(file);
    }

    @Test
    public void testConfigFileReading() {
        
        configurationLoader.loadConfigurationProperties();
        Map<String, Map<String, String>> settingsMap = configurationLoader.getConfigSettings();
        Map<String, String> smtpSettings = settingsMap.get("smtp");
        assert(smtpSettings.size()==5);
    }
    
    @Test
    public void testGlobalSettingsMerging() {
        // TODO
    }
}
