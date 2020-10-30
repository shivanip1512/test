package com.cannontech.common.config;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ConfigurationLoaderTest {

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    SmtpHelper smtpHelper = new SmtpHelper();

    @Before
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
        Assert.assertEquals(smtpSettings.get("mail.smtp.port"), "465");
        Assert.assertEquals(smtpSettings.get("mail.smtp.socketFactory.class"), "javax.net.ssl.SSLSocketFactory");
        Assert.assertEquals(smtpSettings.get("mail.smtp.socketFactory.port"), "465");
        Assert.assertNull(smtpSettings.get("mail.smtp.starttls.enable"));
        Assert.assertEquals(smtpSettings.size(), 3);
    }

}
