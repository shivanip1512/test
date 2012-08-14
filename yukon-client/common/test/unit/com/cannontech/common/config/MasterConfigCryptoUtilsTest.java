package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.MasterConfigCryptoUtils;

public class MasterConfigCryptoUtilsTest {
    private static Set<String> sensitiveData;
    static {
        sensitiveData = new HashSet<String>();
        sensitiveData.add("DB_USERNAME");
        sensitiveData.add("DB_PASSWORD");
        sensitiveData.add("DB_SQLSERVER");
        sensitiveData.add("DB_SQLSERVER_HOST");
        sensitiveData.add("DB_JAVA_URL");
    }

    @Test
    public void test_isSensitiveData() {
        // Some sensitive data
        for (String configKey : sensitiveData) {
            Assert.assertEquals(true,MasterConfigCryptoUtils.isSensitiveData(configKey));
        }

        // Some non-sensitive data
        Assert.assertEquals(false,MasterConfigCryptoUtils.isSensitiveData("DB_FAKE_ENTRY"));
        Assert.assertEquals(false,MasterConfigCryptoUtils.isSensitiveData("ANOTHER_FAKE"));
        Assert.assertEquals(false,MasterConfigCryptoUtils.isSensitiveData("DB_USER_NAME"));
    }

    @Test
    public void test_encryptionDecryptionValue() throws CryptoException {
        String valueEncrypted = MasterConfigCryptoUtils.encryptValue("abc123");
        String valuePlainText = MasterConfigCryptoUtils.decryptValue(valueEncrypted);
        Assert.assertEquals("abc123", valuePlainText);
    }


    @Test
    public void test_isEncrypted() throws CryptoException {
        String valueEncrypted = MasterConfigCryptoUtils.encryptValue("abc123");
        Assert.assertEquals(true,MasterConfigCryptoUtils.isEncrypted(valueEncrypted));
        Assert.assertEquals(true,MasterConfigCryptoUtils.isEncrypted("(AUTO_ENCRYPTED)blablabla"));
        Assert.assertEquals(true,MasterConfigCryptoUtils.isEncrypted("(AUTO_ENCRYPTED) bla bla bla"));
        Assert.assertEquals(true,MasterConfigCryptoUtils.isEncrypted("   (AUTO_ENCRYPTED) blablabla"));
        Assert.assertEquals(true,MasterConfigCryptoUtils.isEncrypted("   (AUTO  _ENCRY  PTED) blablabla"));
        
        Assert.assertEquals(false,MasterConfigCryptoUtils.isEncrypted("abc123"));
    }
}
