package com.cannontech.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.MasterConfigCryptoUtils;

public class MasterConfigCryptoUtilsTest {
    private static Set<MasterConfigString> sensitiveData;
    static {
        sensitiveData = new HashSet<>();
        sensitiveData.add(MasterConfigString.DB_USERNAME);
        sensitiveData.add(MasterConfigString.DB_PASSWORD);
        sensitiveData.add(MasterConfigString.DB_SQLSERVER);
        sensitiveData.add(MasterConfigString.DB_SQLSERVER_HOST);
        sensitiveData.add(MasterConfigString.DB_JAVA_URL);
        sensitiveData.add(MasterConfigString.MAP_DEVICES_KEY);
    }

    @Test
    public void test_isSensitiveData() {
        // Check that all sensitive data (from test list) is marked as encrypted
        for (MasterConfigString configKey : sensitiveData) {
            assertTrue(MasterConfigString.isEncryptedKey(configKey), "Sensitive data is not marked as encrypted.");
        }

        // Check that all data marked as encrypted is sensitive data (from test list)
        for (MasterConfigString configKey : MasterConfigString.values()) {
            if (MasterConfigString.isEncryptedKey(configKey)) {
                assertTrue(sensitiveData.contains(configKey), "Found encrypted data that is not sensitive.");
            }
        }
    }

    @Test
    public void test_licenseKeysEncrypted() {
        // Check that all license keys are marked as encrypted
        for (var licenseKey : MasterConfigLicenseKey.values()) {
            assertTrue(MasterConfigMap.isEncryptedKey(licenseKey.name()), "License key is not marked as encrypted.");
        }
    }

    @Test
    public void test_encryptionDecryptionValue() throws CryptoException {
        String valueEncrypted = MasterConfigCryptoUtils.encryptValue("abc123");
        String valuePlainText = MasterConfigCryptoUtils.decryptValue(valueEncrypted);
        assertEquals("abc123", valuePlainText, "Encrypted/decrypted value does not match original.");
    }


    @Test
    public void test_isEncrypted() throws CryptoException {
        String valueEncrypted = MasterConfigCryptoUtils.encryptValue("abc123");
        assertTrue(MasterConfigCryptoUtils.isEncrypted(valueEncrypted), "Encrypted value parsing failed.");
        assertTrue(MasterConfigCryptoUtils.isEncrypted("(AUTO_ENCRYPTED)blablabla"), "Encrypted value parsing failed.");
        assertTrue(MasterConfigCryptoUtils.isEncrypted("(AUTO_ENCRYPTED) bla bla bla"), "Encrypted value parsing failed.");
        assertTrue(MasterConfigCryptoUtils.isEncrypted("   (AUTO_ENCRYPTED) blablabla"), "Encrypted value parsing failed.");
        assertTrue(MasterConfigCryptoUtils.isEncrypted("   (AUTO  _ENCRY  PTED) blablabla"),"Encrypted value parsing failed.");
        
        assertFalse(MasterConfigCryptoUtils.isEncrypted("abc123"), "Unencrypted value parsing failed.");
    }
}
