package com.cannontech.encryption;

import static org.junit.Assert.fail;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.encryption.impl.AESPasswordBasedCrypto;


public class AESPasswordBasedCryptoTest  {
    byte[] preComputedPlainText = {-85, 68, -55, 107, -106, -119, 42, -106, -73, 84, 126, 19, 52, 60};
    byte[] preComputedCipherText1 = {33, 103, 100, -30, -122, 119, 34, 32, 126, -77, -128, 79, 109, 125, -105, -124, 28, 20, 86, 72, 0, 72, 46, -128, 57, 82, -78, -48, 82, 91, 98, 20, -20, 29, -3, -90, 58, -111, -40, -55, 36, 70, -35, -99, 81, 32, -4, 62, 98, -64, -25, 108, -39, 30, 0, -44, 35, 97, 11, 110, -89, -44, 12, -22};
    byte[] preComputedCipherText2 = {-117, 58, -89, 47, 6, -63, -85, -115, 119, -48, -50, -103, 109, -45, 60, -70, 104, 59, -95, -71, -78, -59, 86, 40, -91, 45, -92, 5, -72, -70, 11, 106, 104, -18, 35, 62, -108, 18, -16, 70, -19, -117, -113, -96, -43, 61, -1, -30, -106, -4, -27, -38, 123, 121, 75, 24, -11, 7, 70, -88, 16, -122, -90, -32};
    byte[] preComputedCipherText3 = {23, 26, 114, -22, -25, 7, 60, 39, 110, -54, 23, -5, -89, -74, 61, -26, -48, -117, -82, -23, 105, 8, -65, -96, 58, 32, -7, -59, 119, -62, -33, -53, 93, -118, 26, 125, -105, 116, 103, -119, -120, 66, -7, -82, -1, -128, 19, -19, -111, -23, 102, -39, 40, 84, -87, 102, -47, 93, -86, -66, -3, 54, -56, -14};
    byte[] badCipherText4 = {24, 26, 114, -22, -25, 7, 60, 39, 110, -54, 23, -5, -89, -74, 61, -26, -48, -117, -82, -23, 105, 8, -65, -96, 58, 32, -7, -59, 119, -62, -33, -53, 93, -118, 26, 125, -105, 116, 103, -119, -120, 66, -7, -82, -1, -128, 19, -19, -111, -23, 102, -39, 40, 84, -87, 102, -47, 93, -86, -66, -3, 54, -56, -14};
    char[] preComputedPassword = {'?','W',')','s','8','!','D','h','I','o','f','1','=','G','F','2'};

    @Test
    public void test_validityToPass1() throws PasswordBasedCryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        Assert.assertEquals(true, aes.isAuthentic(preComputedCipherText1));
    }
    
    @Test
    public void test_validityToPass2() throws PasswordBasedCryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        Assert.assertEquals(true, aes.isAuthentic(preComputedCipherText2));
    }
    
    @Test
    public void test_validityToPass3() throws PasswordBasedCryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        Assert.assertEquals(true, aes.isAuthentic(preComputedCipherText3));
    }
    
    @Test
    public void test_validityToFail() throws PasswordBasedCryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        Assert.assertEquals(false, aes.isAuthentic(badCipherText4));
    }
    
    @Test
    public void test_deryptionWithPassword1() throws PasswordBasedCryptoException, CryptoAuthenticationException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        byte[] plainText1 = aes.decrypt(preComputedCipherText1);
        Assert.assertEquals(true, Arrays.equals(plainText1,preComputedPlainText));
    }
    
    @Test
    public void test_deryptionWithPassword2() throws PasswordBasedCryptoException, CryptoAuthenticationException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        Assert.assertEquals(true, aes.isAuthentic(preComputedCipherText2));
        byte[] plainText2 = aes.decrypt(preComputedCipherText2);
        Assert.assertEquals(true, Arrays.equals(plainText2,preComputedPlainText));
    }

    @Test
    public void test_deryptionWithPassword3() throws PasswordBasedCryptoException, CryptoAuthenticationException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(preComputedPassword);
        Assert.assertEquals(true, aes.isAuthentic(preComputedCipherText3));
        byte[] plainText3 = aes.decrypt(preComputedCipherText3);
        Assert.assertEquals(true, Arrays.equals(plainText3,preComputedPlainText));
    }
    
    @Test
    public void test_encryptionAndDecryptionDefault() throws CryptoAuthenticationException {
        try {
            AESPasswordBasedCrypto aesDefaultConstructor = new AESPasswordBasedCrypto();
            testCipher(aesDefaultConstructor);
        } catch (PasswordBasedCryptoException e) {
            fail();
        }
    }
    
    @Test
    public void test_encryptionAndDecryptionWithPassword() throws PasswordBasedCryptoException, CryptoAuthenticationException {
        char[] password = CryptoUtils.generateRandomPasskey(16);
        AESPasswordBasedCrypto aesWithPassword = new AESPasswordBasedCrypto(password);
        testCipher(aesWithPassword);
    }
    
    @Test
    public void test_encryptionAndDecryptionWithFullConstructor() throws PasswordBasedCryptoException, CryptoAuthenticationException {
        char[] password = CryptoUtils.generateRandomPasskey(16);
        Random rand = new Random();
        int salt = rand.nextInt(2000) + 1;
        int iv = rand.nextInt(2000) + 1;
        int hmac = rand.nextInt(2000) + 1;
        int aes = rand.nextInt(2000) + 1;
        AESPasswordBasedCrypto aesWithPassword = new AESPasswordBasedCrypto(password,salt, iv, hmac, aes);
        testCipher(aesWithPassword);
    }

    private void testCipher(AESPasswordBasedCrypto aes) throws PasswordBasedCryptoException, CryptoAuthenticationException {
        int numBytes = 128;
        int encryptionLayers = 10;
        byte [] knownData = new byte[numBytes];
        byte [] encryptedData = new byte[numBytes];
        byte [] computedData = new byte[numBytes];

        SecureRandom rand = new SecureRandom();
        for (int e=0;e<numBytes;e++) {
            knownData[e] = (byte)rand.nextInt(256);
        }

        // Encrypt a bunch of times
        encryptedData = aes.encrypt(knownData);
        for (int i=0;i<encryptionLayers;i++) {
            encryptedData = aes.encrypt(encryptedData);
        }

        // Decrypt same number of times
        computedData = aes.decrypt(encryptedData);
        for (int i=0;i<encryptionLayers;i++) {
            computedData = aes.decrypt(computedData);
        }

        // Make sure something changed, that we wernt just passing around the same data
        Assert.assertEquals(false, Arrays.equals(encryptedData,computedData));
        Assert.assertEquals(false, Arrays.equals(encryptedData,knownData));

        // Make sure we got plain text back.
        Assert.assertEquals(true, Arrays.equals(knownData,computedData));

    }
}