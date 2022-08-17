package com.cannontech.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class AESPasswordBasedCryptoTest  {
    // Cipher text 1, 2, and 3 should all decrypt to this
    private final byte[] plainText = {-85, 68, -55, 107, -106, -119, 42, -106, -73, 84, 126, 19, 52, 60};
    
    private final byte[] cipherText1 = {33, 103, 100, -30, -122, 119, 34, 32, 126, -77, -128, 79, 109, 125, -105, -124, 28, 20, 86, 72, 0, 72, 46, -128, 57, 82, -78, -48, 82, 91, 98, 20, -20, 29, -3, -90, 58, -111, -40, -55, 36, 70, -35, -99, 81, 32, -4, 62, 98, -64, -25, 108, -39, 30, 0, -44, 35, 97, 11, 110, -89, -44, 12, -22};
    private final byte[] cipherText2 = {-117, 58, -89, 47, 6, -63, -85, -115, 119, -48, -50, -103, 109, -45, 60, -70, 104, 59, -95, -71, -78, -59, 86, 40, -91, 45, -92, 5, -72, -70, 11, 106, 104, -18, 35, 62, -108, 18, -16, 70, -19, -117, -113, -96, -43, 61, -1, -30, -106, -4, -27, -38, 123, 121, 75, 24, -11, 7, 70, -88, 16, -122, -90, -32};
    private final byte[] cipherText3 = {23, 26, 114, -22, -25, 7, 60, 39, 110, -54, 23, -5, -89, -74, 61, -26, -48, -117, -82, -23, 105, 8, -65, -96, 58, 32, -7, -59, 119, -62, -33, -53, 93, -118, 26, 125, -105, 116, 103, -119, -120, 66, -7, -82, -1, -128, 19, -19, -111, -23, 102, -39, 40, 84, -87, 102, -47, 93, -86, -66, -3, 54, -56, -14};
    private final byte[] badCipherText4 = {24, 26, 114, -22, -25, 7, 60, 39, 110, -54, 23, -5, -89, -74, 61, -26, -48, -117, -82, -23, 105, 8, -65, -96, 58, 32, -7, -59, 119, -62, -33, -53, 93, -118, 26, 125, -105, 116, 103, -119, -120, 66, -7, -82, -1, -128, 19, -19, -111, -23, 102, -39, 40, 84, -87, 102, -47, 93, -86, -66, -3, 54, -56, -14};
    
    private final char[] password = {'?','W',')','s','8','!','D','h','I','o','f','1','=','G','F','2'};

    @Test
    public void testBlank() throws CryptoException, DecoderException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        String blankEncrypted = aes.encryptToHexStr("");
        String blank = aes.decryptHexStr(blankEncrypted);
        assertTrue("".equals(blank));
    }

    /**
     * Tests the encryption/decryption methods which take strings
     * @throws DecoderException 
     */
    @Test
    public void test_encryptToHex_decryptHex() throws CryptoException, DecoderException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        String helloStr = "hello world";
        String helloStrHexEncrypted_1 = aes.encryptToHexStr(helloStr);
        String helloStrHexEncrypted_2 = aes.encryptToHexStr(helloStr);
        String helloStrDecrypted = aes.decryptHexStr(helloStrHexEncrypted_1);

        assertFalse(helloStr.equals(helloStrHexEncrypted_1));
        assertFalse(helloStrDecrypted.equals(helloStrHexEncrypted_1));
        assertTrue(helloStr.equals(helloStrDecrypted));

        // Same thing encrypted twice should result in two different strings
        assertFalse(helloStrHexEncrypted_2.equals(helloStrHexEncrypted_1));
    }

    /**
     * Tests the isAuthentic method against cipherText1
     */
    @Test
    public void test_validityToPass1() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        assertTrue(aes.isAuthentic(cipherText1));
    }

    /**
     * Tests the isAuthentic method against cipherText2
     */
    @Test
    public void test_validityToPass2() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        assertTrue(aes.isAuthentic(cipherText2));
    }

    /**
     * Tests the isAuthentic method against cipherText3
     */
    @Test
    public void test_validityToPass3() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        assertTrue(aes.isAuthentic(cipherText3));
    }

    /**
     * Tests the isAuthentic method against cipherText4 (test to fail)
     */
    @Test(expected=CryptoException.class)
    public void test_validityToFail() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        aes.verifyAuthenticity(badCipherText4);
    }

    /**
     * Tests decrypt() method against cipherText1
     * cipherText1 should decrypt to plainText
     */
    @Test
    public void test_deryptionWithPassword1() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        byte[] plainText1 = aes.decrypt(cipherText1);
        assertEquals(true, Arrays.equals(plainText1,plainText));
    }

    /**
     * Tests decrypt() method against cipherText2
     * cipherText2 should decrypt to plainText
     */
    @Test
    public void test_deryptionWithPassword2() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        assertEquals(true, aes.isAuthentic(cipherText2));
        byte[] plainText2 = aes.decrypt(cipherText2);
        assertEquals(true, Arrays.equals(plainText2,plainText));
    }

    /**
     * Tests decrypt() method against cipherText3
     * cipherText3 should decrypt to plainText
     */
    @Test
    public void test_deryptionWithPassword3() throws CryptoException {
        AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(password);
        assertEquals(true, aes.isAuthentic(cipherText3));
        byte[] plainText3 = aes.decrypt(cipherText3);
        assertEquals(true, Arrays.equals(plainText3,plainText));
    }

    @Test
    public void test_encryptionAndDecryptionWithPassword() throws CryptoException {
        char[] password = CryptoUtils.generateRandomPasskey(16);
        AESPasswordBasedCrypto aesWithPassword = new AESPasswordBasedCrypto(password);
        testCipher(aesWithPassword);
    }

    @Test
    public void test_multipleEncryptionOnSamePlaintext() throws CryptoException {
        int testIterations = 10;
        String plainTextStr = "This test should show this string encrypting to values that are different each time";
        char[] password = CryptoUtils.generateRandomPasskey(16);
        byte[] cipherText = null;
        List<byte[]> cipherTexts = new ArrayList<byte[]>();
        AESPasswordBasedCrypto aesWithPassword = new AESPasswordBasedCrypto(password);

        for(int i=0;i<testIterations;i++) {
            cipherText = aesWithPassword.encrypt(plainTextStr.getBytes());
            for(byte[] testBlob : cipherTexts) {
                assertEquals(false, Arrays.equals(testBlob, cipherText));
            }
            cipherTexts.add(cipherText);
        }
    }

    /**
     * Tests the cipher by encrypting randomly generated data 10 times,
     * then decrypts 10 more to arrive at plaintext. 
     */
    private void testCipher(AESPasswordBasedCrypto aes) throws CryptoException {
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

        // Make sure something changed, that we weren't just passing around the same data
        assertEquals(false, Arrays.equals(encryptedData,computedData));
        assertEquals(false, Arrays.equals(encryptedData,knownData));

        // Make sure we got plain text back.
        assertEquals(true, Arrays.equals(knownData,computedData));
    }
}