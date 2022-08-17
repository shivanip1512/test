package com.cannontech.common.config;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ConfigurationLoaderTest {

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    SmtpHelper smtpHelper = new SmtpHelper();

    @Before
    public void setup() {

        ReflectionTestUtils.setField(smtpHelper, "configurationLoader", configurationLoader);
        GlobalSettingDao mockGlobalSettingDao = EasyMock.createMock(GlobalSettingDao.class);
        ReflectionTestUtils.setField(smtpHelper, "globalSettingDao", mockGlobalSettingDao);
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.SMTP_HOST)).andReturn("xyz.com");
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.SMTP_PORT)).andReturn("568");
        EasyMock.expect(mockGlobalSettingDao.getEnum(GlobalSettingType.SMTP_ENCRYPTION_TYPE, SmtpEncryptionType.class)).andReturn(SmtpEncryptionType.SSL);
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD)).andReturn("xyz");
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.SMTP_USERNAME)).andReturn("abc");
        EasyMock.replay(mockGlobalSettingDao);

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
        Assert.assertNull(smtpHelper.getSmtpConfigSettings().get("mail.smtp.starttls.enable"));
        Assert.assertEquals(smtpSettings.size(), 3);
    }

    @Test
    public void testGlobalSettingsMerging() {
        smtpHelper.reloadSettings();
        Assert.assertEquals(smtpHelper.getSmtpConfigSettings().get("mail.smtp.host"), "xyz.com");
        // File property overwrites the global settings for port.
        Assert.assertEquals(smtpHelper.getSmtpConfigSettings().get("mail.smtp.port"), "465");
        Assert.assertEquals(smtpHelper.getSmtpConfigSettings().get("mail.smtp.socketFactory.class"),
            "javax.net.ssl.SSLSocketFactory");
        Assert.assertEquals(smtpHelper.getSmtpConfigSettings().get("mail.smtp.socketFactory.port"), "465");
        Assert.assertNull(smtpHelper.getSmtpConfigSettings().get("mail.smtp.starttls.enable"));
    }
}
