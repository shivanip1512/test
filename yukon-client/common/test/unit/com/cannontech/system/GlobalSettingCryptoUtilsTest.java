package com.cannontech.system;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.encryption.CryptoException;

public class GlobalSettingCryptoUtilsTest {

    @Test
    public void test_encryptionDecryptionValue() throws CryptoException, IOException, JDOMException, DecoderException {
        String valueEncrypted = null;
        String valuePlainText = null;

        valueEncrypted = GlobalSettingCryptoUtils.encryptValue("abc123");
        valuePlainText = GlobalSettingCryptoUtils.decryptValue(valueEncrypted);
        Assert.assertEquals("Encrypted/decrypted value does not match original.", "abc123", valuePlainText);

        valueEncrypted = GlobalSettingCryptoUtils.encryptValue("abc123");
        valuePlainText = GlobalSettingCryptoUtils.decryptValue(valueEncrypted);
        Assert.assertNotEquals("Encrypted/decrypted value does not match original.", "abc1231", valuePlainText);
    }

    @Test
    public void test_isEncrypted() throws CryptoException, IOException, JDOMException {
        String valueEncrypted = GlobalSettingCryptoUtils.encryptValue("abc123");
        Assert.assertTrue("Encrypted value parsing failed.", GlobalSettingCryptoUtils.isEncrypted(valueEncrypted));
        Assert.assertTrue("Encrypted value parsing failed.", GlobalSettingCryptoUtils.isEncrypted("(AUTO_ENCRYPTED)abc123"));
        Assert.assertTrue("Encrypted value parsing failed.", GlobalSettingCryptoUtils.isEncrypted("(AUTO_ENCRYPTED) a b c 1 2 3"));
        Assert.assertTrue("Encrypted value parsing failed.", GlobalSettingCryptoUtils.isEncrypted("   (AUTO_ENCRYPTED) abc123"));
        Assert.assertTrue("Encrypted value parsing failed.", GlobalSettingCryptoUtils.isEncrypted("   (AUTO  _ENCRY  PTED) abc123"));
        
        Assert.assertFalse("Unencrypted value parsing failed.", GlobalSettingCryptoUtils.isEncrypted("abc123"));
    }
}
