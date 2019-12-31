package com.cannontech.encryption;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.junit.Test;

public class SystemPublisherMetadataEncryptionTest {

    @Test
    public void test_encrypt_decrypt_valid() throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Test String";
        String encryptedString = SystemPublisherMetadataEncryption.encrypt(strToEncrypt);
        assertFalse("Original string and encrypted string are same : ", strToEncrypt.equals(encryptedString));
        String decryptedString = SystemPublisherMetadataEncryption.decrypt(encryptedString);
        assertTrue("Original string and encrypted string are not same : ", strToEncrypt.equals(decryptedString));
    }

    @Test(expected = Exception.class)
    public void test_encrypt_decrypt_invalid()
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Test String";
        String encryptedString = SystemPublisherMetadataEncryption.encrypt(strToEncrypt);
        assertFalse("Original string and encrypted string are same : ", strToEncrypt.equals(encryptedString));
        SystemPublisherMetadataEncryption.decrypt(encryptedString + "Extra Param");
    }

    @Test
    public void test_setChiper_valid() throws Exception {
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("setChiper", Integer.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class, 1);
    }

    @Test(expected = Exception.class)
    public void test_setChiper_invalid() throws Exception {
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("setChiper", Integer.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class, -999999);
    }

    @Test
    public void test_setKey_valid() throws Exception {
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("setKey");
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class);
    }

    @Test
    public void test_encryptLine_skipLine() throws Exception {
        String stringNotToEncrypt = "Key: Value";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class, stringNotToEncrypt, false, false, stringBuilder);
        assertTrue("Original string and encrypted string are not same : ",
                stringNotToEncrypt.equals(stringBuilder.toString().trim()));
    }

    @Test
    public void test_encryptLine_singleLineSource() throws Exception {
        String stringToEncrypt = "    source: Some SQL Query";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class, stringToEncrypt, true, false, stringBuilder);
        assertTrue("Original string and encrypted string are same : ",
                !stringToEncrypt.equals(stringBuilder.toString().trim()));

        String values[] = stringBuilder.toString().split(":", 2);
        assertTrue("source text should not get encrypted ", values[0].equals("    source"));
        assertTrue("SQL query should get encrypted", !values[1].trim().equals(" Some SQL Query"));
    }

    @Test
    public void test_encryptLine_multiLineSource_firstLine() throws Exception {
        String stringNotToEncrypt = "    source: >-";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class, stringNotToEncrypt, false, false, stringBuilder);
        assertTrue("Original string and encrypted string are not same : ",
                stringNotToEncrypt.contains(stringBuilder.toString().trim()));
    }

    @Test
    public void test_encryptLine_multiLineSource_secondLine() throws Exception {
        String stringToEncrypt = "Some SQL to be encrypted";
        StringBuilder stringBuilder = new StringBuilder();
        Class<SystemPublisherMetadataEncryption> encryptionClass = SystemPublisherMetadataEncryption.class;
        Method method = encryptionClass.getDeclaredMethod("encryptLine", String.class, Boolean.class, Boolean.class,
                StringBuilder.class);
        method.setAccessible(true);
        method.invoke(SystemPublisherMetadataEncryption.class, stringToEncrypt, true, true, stringBuilder);
        assertTrue("Original string and encrypted string are same : ",
                !stringToEncrypt.equals(stringBuilder.toString().trim()));
    }

}
