package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.MasterConfigCryptoUtils;

public class MasterConfigCryptoUtilsTest {
    private static Set<MasterConfigStringKeysEnum> sensitiveData;
    static {
        sensitiveData = new HashSet<MasterConfigStringKeysEnum>();
        sensitiveData.add(MasterConfigStringKeysEnum.DB_USERNAME);
        sensitiveData.add(MasterConfigStringKeysEnum.DB_PASSWORD);
        sensitiveData.add(MasterConfigStringKeysEnum.DB_SQLSERVER);
        sensitiveData.add(MasterConfigStringKeysEnum.DB_SQLSERVER_HOST);
        sensitiveData.add(MasterConfigStringKeysEnum.DB_JAVA_URL);
        sensitiveData.add(MasterConfigStringKeysEnum.SUPPORT_BUNDLE_FTP_UPLOAD_USER);
        sensitiveData.add(MasterConfigStringKeysEnum.SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD);
        sensitiveData.add(MasterConfigStringKeysEnum.SUPPORT_BUNDLE_FTP_UPLOAD_HOST);
    }

    @Test
    public void test_isSensitiveData() {
        // Some sensitive data
        for (MasterConfigStringKeysEnum configKey : sensitiveData) {
            Assert.assertEquals(true, MasterConfigStringKeysEnum.isEncryptedKey(configKey));
        }

        // Some non-sensitive data
        Assert.assertEquals(false ,MasterConfigStringKeysEnum.isEncryptedKey(MasterConfigStringKeysEnum.CMEP_UNITS));
        Assert.assertEquals(false, MasterConfigStringKeysEnum.isEncryptedKey(MasterConfigStringKeysEnum.CYME_REPORT_NAME));
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
