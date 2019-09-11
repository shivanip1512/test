package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.MasterConfigCryptoUtils;

public class MasterConfigCryptoUtilsTest {
    private static Set<MasterConfigString> sensitiveData;
    static {
        sensitiveData = new HashSet<>();
        sensitiveData.add(MasterConfigString.CAP_CONTROL_AMFM_DB_USERNAME);
        sensitiveData.add(MasterConfigString.CAP_CONTROL_AMFM_DB_PASSWORD);
        sensitiveData.add(MasterConfigString.DB_USERNAME);
        sensitiveData.add(MasterConfigString.DB_PASSWORD);
        sensitiveData.add(MasterConfigString.DB_SQLSERVER);
        sensitiveData.add(MasterConfigString.DB_SQLSERVER_HOST);
        sensitiveData.add(MasterConfigString.DB_JAVA_URL);
        sensitiveData.add(MasterConfigString.MAP_DEVICES_KEY);
        sensitiveData.add(MasterConfigString.SUPPORT_BUNDLE_FTP_UPLOAD_USER);
        sensitiveData.add(MasterConfigString.SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD);
        sensitiveData.add(MasterConfigString.SUPPORT_BUNDLE_FTP_UPLOAD_HOST);
    }

    @Test
    public void test_isSensitiveData() {
        // Check that all sensitive data (from test list) is marked as encrypted
        for (MasterConfigString configKey : sensitiveData) {
            Assert.assertTrue("Sensitive data is not marked as encrypted.", MasterConfigString.isEncryptedKey(configKey));
        }

        // Check that all data marked as encrypted is sensitive data (from test list)
        for (MasterConfigString configKey : MasterConfigString.values()) {
            if (MasterConfigString.isEncryptedKey(configKey)) {
                Assert.assertTrue("Found encrypted data that is not sensitive.", sensitiveData.contains(configKey));
            }
        }
    }

    @Test
    public void test_licenseKeysEncrypted() {
        // Check that all license keys are marked as encrypted
        for (var licenseKey : MasterConfigLicenseKey.values()) {
            Assert.assertTrue("License key is not marked as encrypted.", MasterConfigMap.isEncryptedKey(licenseKey.name()));
        }
    }

    @Test
    public void test_encryptionDecryptionValue() throws CryptoException {
        String valueEncrypted = MasterConfigCryptoUtils.encryptValue("abc123");
        String valuePlainText = MasterConfigCryptoUtils.decryptValue(valueEncrypted);
        Assert.assertEquals("Encrypted/decrypted value does not match original.", "abc123", valuePlainText);
    }


    @Test
    public void test_isEncrypted() throws CryptoException {
        String valueEncrypted = MasterConfigCryptoUtils.encryptValue("abc123");
        Assert.assertTrue("Encrypted value parsing failed.", MasterConfigCryptoUtils.isEncrypted(valueEncrypted));
        Assert.assertTrue("Encrypted value parsing failed.", MasterConfigCryptoUtils.isEncrypted("(AUTO_ENCRYPTED)blablabla"));
        Assert.assertTrue("Encrypted value parsing failed.", MasterConfigCryptoUtils.isEncrypted("(AUTO_ENCRYPTED) bla bla bla"));
        Assert.assertTrue("Encrypted value parsing failed.", MasterConfigCryptoUtils.isEncrypted("   (AUTO_ENCRYPTED) blablabla"));
        Assert.assertTrue("Encrypted value parsing failed.", MasterConfigCryptoUtils.isEncrypted("   (AUTO  _ENCRY  PTED) blablabla"));
        
        Assert.assertFalse("Unencrypted value parsing failed.", MasterConfigCryptoUtils.isEncrypted("abc123"));
    }
}
