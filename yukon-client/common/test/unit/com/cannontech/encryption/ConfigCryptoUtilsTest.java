package com.cannontech.encryption;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.jdom2.JDOMException;
import org.junit.Test;

import com.cannontech.system.GlobalSettingCryptoUtils;

public class ConfigCryptoUtilsTest {
    
    @Test
    public void test_encrypt_decrypt_trims_spaces() throws CryptoException, IOException, JDOMException, DecoderException {
        String strWithPaddingSpaces = "    Test String    ";
        String strWithoutPaddingSpaces = "Test String";
        String strWithInternalSpaces = "Test   String";
        String strWithNoSpaces = "TestStringNoSpaces!@#$%^++=";
        
        String encryptedStrWithPaddingSpaces = GlobalSettingCryptoUtils.encryptValue(strWithPaddingSpaces);
        assertTrue("Encrypted string was changed unexpectedly during encryption/decryption", GlobalSettingCryptoUtils.decryptValue(encryptedStrWithPaddingSpaces).equals(strWithoutPaddingSpaces));
        
        String encryptedStrWithInternalSpaces = GlobalSettingCryptoUtils.encryptValue(strWithInternalSpaces);
        assertTrue("Encrypted string was changed unexpectedly during encryption/decryption", GlobalSettingCryptoUtils.decryptValue(encryptedStrWithInternalSpaces).equals(strWithInternalSpaces));
        
        String encryptedStrWithNoSpaces = GlobalSettingCryptoUtils.encryptValue(strWithNoSpaces);
        assertTrue("Encrypted string was changed unexpectedly during encryption/decryption", GlobalSettingCryptoUtils.decryptValue(encryptedStrWithNoSpaces).equals(strWithNoSpaces));
    }
    
    @Test
    public void test_encrypt_decrypt_different_whitespace() throws CryptoException, IOException, JDOMException, DecoderException {
        String strWithTabsAndNewlines = "\tTest String With\tTabs\t\n\t";
        String trimmedString = "Test String With\tTabs";
        
        String encryptedString = GlobalSettingCryptoUtils.encryptValue(strWithTabsAndNewlines);
        assertTrue("Encrypted string was changed unexpectedly during encryption/decryption", GlobalSettingCryptoUtils.decryptValue(encryptedString).equals(trimmedString));
    }
    
    @Test public void test_encrypt_decrypt_string_with_newline() throws CryptoException, IOException, JDOMException, DecoderException {
        String strWithNewlines = "\n\nTest\nString\n";
        String strWithNewLinesTrimmed = "Test\nString";
        
        String encryptedString = GlobalSettingCryptoUtils.encryptValue(strWithNewlines);
        assertTrue("Encrypted string was changed unexpectedly during encryption/decryption", GlobalSettingCryptoUtils.decryptValue(encryptedString).equals(strWithNewLinesTrimmed));
    }

}
