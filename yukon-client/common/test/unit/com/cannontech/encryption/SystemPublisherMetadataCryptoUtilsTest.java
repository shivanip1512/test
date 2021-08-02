package com.cannontech.encryption;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemPublisherMetadataCryptoUtilsTest {

    @Test
    public void test_encrypt_decrypt_valid() throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Test String";
        String encryptedString = SystemPublisherMetadataCryptoUtils.encrypt(strToEncrypt);
        assertFalse(strToEncrypt.equals(encryptedString), "Original string and encrypted string are same : ");
        String decryptedString = SystemPublisherMetadataCryptoUtils.decrypt(encryptedString);
        assertTrue(strToEncrypt.equals(decryptedString), "Original string and encrypted string are not same : ");
    }

    @Test
    public void test_encrypt_decrypt_invalid()
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Test String";
        String encryptedString = SystemPublisherMetadataCryptoUtils.encrypt(strToEncrypt);
        assertFalse(strToEncrypt.equals(encryptedString), "Original string and encrypted string are same : ");
        Assertions.assertThrows(Exception.class, () -> {
            SystemPublisherMetadataCryptoUtils.decrypt(encryptedString + "Extra Param");
        });
    }

    @Test
    public void test_setChiper_valid() throws Exception {
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("setChiper", Integer.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataCryptoUtils.class, 1);
    }

    @Test
    public void test_setChiper_invalid() throws Exception {
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("setChiper", Integer.class);
        method.setAccessible(true);
        Assertions.assertThrows(Exception.class, () -> {
            ;method.invoke(SystemPublisherMetadataCryptoUtils.class, -999999);
        });
    }

    @Test
    public void test_setKey_valid() throws Exception {
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("setKey");
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataCryptoUtils.class);
    }

    @Test
    public void test_encryptLine_skipLine() throws Exception {
        String stringNotToEncrypt = "Key: Value";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataCryptoUtils.class, stringNotToEncrypt, false, false, stringBuilder);
        assertTrue(stringNotToEncrypt.equals(stringBuilder.toString().trim()),
                "Original string and encrypted string are not same : ");
    }

    @Test
    public void test_encryptLine_singleLineSource() throws Exception {
        String stringToEncrypt = "    source: Some SQL Query";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataCryptoUtils.class, stringToEncrypt, true, false, stringBuilder);
        assertTrue(!stringToEncrypt.equals(stringBuilder.toString().trim()),
                "Original string and encrypted string are same : ");

        String values[] = stringBuilder.toString().split(":", 2);
        assertTrue(values[0].equals("    source"), "source text should not get encrypted ");
        assertTrue(!values[1].trim().equals(" Some SQL Query"), "SQL query should get encrypted");
    }

    @Test
    public void test_encryptLine_multiLineSource_firstLine() throws Exception {
        String stringNotToEncrypt = "    source: >-";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataCryptoUtils.class, stringNotToEncrypt, false, false, stringBuilder);
        assertTrue(stringNotToEncrypt.contains(stringBuilder.toString().trim()),
                "Original string and encrypted string are not same : ");
    }

    @Test
    public void test_encryptLine_multiLineSource_secondLine() throws Exception {
        String stringToEncrypt = "Some SQL to be encrypted";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataCryptoUtils> encryptionClass = SystemPublisherMetadataCryptoUtils.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataCryptoUtils.class, stringToEncrypt, true, true, stringBuilder);
        assertTrue(!stringToEncrypt.equals(stringBuilder.toString().trim()),
                "Original string and encrypted string are same : ");
    }

}
