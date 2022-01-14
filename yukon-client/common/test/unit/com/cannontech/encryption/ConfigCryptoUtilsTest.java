package com.cannontech.encryption;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;

import com.cannontech.system.GlobalSettingCryptoUtils;

public class ConfigCryptoUtilsTest {
    
    @Test
    public void test_encrypt_decrypt_trims_spaces() throws CryptoException, IOException, JDOMException, DecoderException {
        String strWithPaddingSpaces = "    Test String    ";
        String strWithoutPaddingSpaces = "Test String";
        String strWithInternalSpaces = "Test   String";
        String strWithNoSpaces = "TestStringNoSpaces!@#$%^++=";
        
        String encryptedStrWithPaddingSpaces = GlobalSettingCryptoUtils.encryptValue(strWithPaddingSpaces);
        assertTrue(GlobalSettingCryptoUtils.decryptValue(encryptedStrWithPaddingSpaces).equals(strWithoutPaddingSpaces),
                "Encrypted string was changed unexpectedly during encryption/decryption");
        
        String encryptedStrWithInternalSpaces = GlobalSettingCryptoUtils.encryptValue(strWithInternalSpaces);
        assertTrue(GlobalSettingCryptoUtils.decryptValue(encryptedStrWithInternalSpaces).equals(strWithInternalSpaces),
                "Encrypted string was changed unexpectedly during encryption/decryption");
        
        String encryptedStrWithNoSpaces = GlobalSettingCryptoUtils.encryptValue(strWithNoSpaces);
        assertTrue(GlobalSettingCryptoUtils.decryptValue(encryptedStrWithNoSpaces).equals(strWithNoSpaces),
                "Encrypted string was changed unexpectedly during encryption/decryption");
    }
    
    @Test
    public void test_encrypt_decrypt_different_whitespace() throws CryptoException, IOException, JDOMException, DecoderException {
        String strWithTabsAndNewlines = "\tTest String With\tTabs\t\n\t";
        String trimmedString = "Test String With\tTabs";
        
        String encryptedString = GlobalSettingCryptoUtils.encryptValue(strWithTabsAndNewlines);
        assertTrue(GlobalSettingCryptoUtils.decryptValue(encryptedString).equals(trimmedString),
                "Encrypted string was changed unexpectedly during encryption/decryption");
    }
    
    @Test public void test_encrypt_decrypt_string_with_newline() throws CryptoException, IOException, JDOMException, DecoderException {
        String strWithNewlines = "\n\nTest\nString\n";
        String strWithNewLinesTrimmed = "Test\nString";
        
        String encryptedString = GlobalSettingCryptoUtils.encryptValue(strWithNewlines);
        assertTrue(GlobalSettingCryptoUtils.decryptValue(encryptedString).equals(strWithNewLinesTrimmed),
                "Encrypted string was changed unexpectedly during encryption/decryption");
    }

    @Test 
    public void test_encrypt_string_with_null() throws CryptoException, IOException, JDOMException, DecoderException {
        String encryptedString = ConfigCryptoUtils.encryptValue(null, null);
        assertTrue(encryptedString == null);
    }
}
