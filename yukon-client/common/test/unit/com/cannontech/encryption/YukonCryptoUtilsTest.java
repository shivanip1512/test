package com.cannontech.encryption;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class YukonCryptoUtilsTest {

    @Test
    public void test_encrypt_decrypt_valid() throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Test String";
        String encryptedString = YukonCryptoUtils.encrypt(strToEncrypt);
        assertFalse(strToEncrypt.equals(encryptedString), "Original string and encrypted string are same : ");
        String decryptedString = YukonCryptoUtils.decrypt(encryptedString);
        assertTrue(strToEncrypt.equals(decryptedString), "Original string and encrypted string are not same : ");
    }

    @Test
    public void test_encrypt_decrypt_invalid()
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Test String";
        String encryptedString = YukonCryptoUtils.encrypt(strToEncrypt);
        assertFalse(strToEncrypt.equals(encryptedString), "Original string and encrypted string are same : ");
        Assertions.assertThrows(Exception.class, () -> {
            YukonCryptoUtils.decrypt(encryptedString + "Extra Param");
        });
    }

    @Test
    public void test_setChiper_valid() throws Exception {
        Class<YukonCryptoUtils> encryptionClass = YukonCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("setChiper", Integer.class);
        method.setAccessible(true);
        method.invoke(YukonCryptoUtils.class, 1);
    }

    @Test
    public void test_setChiper_invalid() throws Exception {
        Class<YukonCryptoUtils> encryptionClass = YukonCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("setChiper", Integer.class);
        method.setAccessible(true);
        Assertions.assertThrows(Exception.class, () -> {
            ;method.invoke(YukonCryptoUtils.class, -999999);
        });
    }

    @Test
    public void test_setKey_valid() throws Exception {
        Class<YukonCryptoUtils> encryptionClass = YukonCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("setKey");
        method.setAccessible(true);
        method.invoke(YukonCryptoUtils.class);
    }

}
