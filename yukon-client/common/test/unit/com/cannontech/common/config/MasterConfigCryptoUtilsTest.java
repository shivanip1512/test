package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.MasterConfigCryptoUtils;

public class MasterConfigCryptoUtilsTest {
    private static Set<MasterConfigString> sensitiveData;
    static {
        sensitiveData = new HashSet<MasterConfigString>();
        sensitiveData.add(MasterConfigString.DB_USERNAME);
        sensitiveData.add(MasterConfigString.DB_PASSWORD);
        sensitiveData.add(MasterConfigString.DB_SQLSERVER);
        sensitiveData.add(MasterConfigString.DB_SQLSERVER_HOST);
        sensitiveData.add(MasterConfigString.DB_JAVA_URL);
        sensitiveData.add(MasterConfigString.SUPPORT_BUNDLE_FTP_UPLOAD_USER);
        sensitiveData.add(MasterConfigString.SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD);
        sensitiveData.add(MasterConfigString.SUPPORT_BUNDLE_FTP_UPLOAD_HOST);
    }

    @Test
    public void test_isSensitiveData() {
        // Some sensitive data
        for (MasterConfigString configKey : sensitiveData) {
            Assert.assertEquals(true, MasterConfigString.isEncryptedKey(configKey));
        }

        // Some non-sensitive data
        Assert.assertEquals(false ,MasterConfigString.isEncryptedKey(MasterConfigString.CMEP_UNITS));
        Assert.assertEquals(false, MasterConfigString.isEncryptedKey(MasterConfigString.CYME_REPORT_NAME));
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
