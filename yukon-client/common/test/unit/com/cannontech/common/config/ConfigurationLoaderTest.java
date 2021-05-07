package com.cannontech.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ConfigurationLoaderTest {

    ConfigurationLoader configurationLoader = new ConfigurationLoader();

    @BeforeEach
    public void setup() {
        File file = null;
        try {
            file = new File(getClass().getResource("configuration.properties").toURI());
        } catch (URISyntaxException e) {
            file = new File(getClass().getResource("configuration.properties").getPath());
        }
        ReflectionTestUtils.setField(configurationLoader, "configFileLocation", file);
        configurationLoader.loadConfigurationProperties();
    }

    @Test
    public void testConfigFileReading() {
        Map<String, Map<String, String>> settingsMap = configurationLoader.getConfigSettings();
        Map<String, String> smtpSettings = settingsMap.get("smtp");
        assertEquals(smtpSettings.get("mail.smtp.port"), "465");
        assertEquals(smtpSettings.get("mail.smtp.socketFactory.class"), "javax.net.ssl.SSLSocketFactory");
        assertEquals(smtpSettings.get("mail.smtp.socketFactory.port"), "465");
        assertNull(smtpSettings.get("mail.smtp.starttls.enable"));
        assertEquals(smtpSettings.size(), 3);
    }

}
