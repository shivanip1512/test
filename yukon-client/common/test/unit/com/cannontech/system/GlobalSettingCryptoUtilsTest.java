package com.cannontech.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;

import com.cannontech.encryption.CryptoException;

public class GlobalSettingCryptoUtilsTest {

    @Test
    public void test_encryptionDecryptionValue() throws CryptoException, IOException, JDOMException, DecoderException {
        String valueEncrypted = null;
        String valuePlainText = null;

        valueEncrypted = GlobalSettingCryptoUtils.encryptValue("abc123");
        valuePlainText = GlobalSettingCryptoUtils.decryptValue(valueEncrypted);
        assertEquals("abc123", valuePlainText, "Encrypted/decrypted value does not match original.");

        valueEncrypted = GlobalSettingCryptoUtils.encryptValue("abc123");
        valuePlainText = GlobalSettingCryptoUtils.decryptValue(valueEncrypted);
        assertNotEquals("abc1231", valuePlainText, "Encrypted/decrypted value does not match original.");
    }

    @Test
    public void test_isEncrypted() throws CryptoException, IOException, JDOMException {
        String valueEncrypted = GlobalSettingCryptoUtils.encryptValue("abc123");
        assertTrue(GlobalSettingCryptoUtils.isEncrypted(valueEncrypted), "Encrypted value parsing failed.");
        assertTrue(GlobalSettingCryptoUtils.isEncrypted("(AUTO_ENCRYPTED)abc123"), "Encrypted value parsing failed.");
        assertTrue(GlobalSettingCryptoUtils.isEncrypted("(AUTO_ENCRYPTED) a b c 1 2 3"), "Encrypted value parsing failed.");
        assertTrue(GlobalSettingCryptoUtils.isEncrypted("   (AUTO_ENCRYPTED) abc123"), "Encrypted value parsing failed.");
        assertTrue(GlobalSettingCryptoUtils.isEncrypted("   (AUTO  _ENCRY  PTED) abc123"), "Encrypted value parsing failed.");
        
        assertFalse(GlobalSettingCryptoUtils.isEncrypted("abc123"), "Unencrypted value parsing failed.");
    }
}
