package com.cannontech.encryption.impl;

import java.security.Key;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.PasswordBasedCrypto;

public class AESPasswordBasedCrypto implements PasswordBasedCrypto {

    private Cipher encryptingCipher;
    private Cipher decryptingCipher;
    private Mac hMac;

    /************************
      Default Encryption Values. Do not change!
      Reason:  Changing these values will make any data that was
               encrypted using these default values useless.
               The CryptoUtils.deriveSecret(..) method uses these 
               values to determine how many times the password will
               be hashed to return the secret array of bytes. Even one
               more iteration and the result is completely garbage. 
               So please consider not changing these values unless 
               absolutely necessary.
     */
    private static final int keyByteLength = 16;
    private static final int defaultSaltIters = 2005;
    private static final int defaultIvIters = 4019;
    private static final int defaultHmacKeyIters = 7003;
    private static final int defaultAesKeyIters = 10098;
    private static final int numRandomBytes = 15;

    /**
     * Calls AESPasswordBasedSymmetricCrypto(char[] password, int saltIters, int ivIters, int hmacKeyIters, int aesKeyIters)
     * with values saltIters=2005, ivIters=4019, hmacKeyIters=7003, aesKeyIters=10098.
     *      and generates a random password using CryptoUtils.generateRandomPasskey(16)
     *
     * @throws PasswordBasedCryptoException
     */
    public AESPasswordBasedCrypto() throws CryptoException {
        this(CryptoUtils.generateRandomPasskey(keyByteLength));
    }

    /**
     * Calls AESPasswordBasedSymmetricCrypto(char[] password, int saltIters, int ivIters, int hmacKeyIters, int aesKeyIters)
     * with values saltIters=2005, ivIters=4019, hmacKeyIters=7003, aesKeyIters=10098.
     * 
     * @param password : char[] - password which is used to derive data used by the cipher
     * @throws PasswordBasedCryptoException
     */
    public AESPasswordBasedCrypto(char[] password) throws CryptoException {
        this(password,defaultSaltIters,defaultIvIters,defaultHmacKeyIters,defaultAesKeyIters);
    }

    /**
     * Sets up the AES cipher using CBC mode and PKCS#5 padding. Derives the salt, initilization vector,
     * HMAC key, and AES key based on the password using CryptoUtils.deriveSecret(..) method.
     * The ciphers use HMAC for authentication using SHA256 hashes.
     * A password with a high amount of information entropy is suggested.
     * 
     * @param password : char[] - password which is used to derive data used by the cipher
     * @param saltIters : int
     * @param ivIters : int
     * @param hmacKeyIters : int
     * @param aesKeyIters : int
     * 
     * @throws PasswordBasedCryptoException
     */
    public AESPasswordBasedCrypto(char[] password, int saltIters, int ivIters, int hmacKeyIters, int aesKeyIters) throws CryptoException {
        try {
            Security.addProvider(new BouncyCastleProvider());

            byte[] salt = CryptoUtils.pbkdf2(password, keyByteLength, CryptoUtils.getYukonsalt(), saltIters);
            byte[] initVector = CryptoUtils.pbkdf2(password, keyByteLength, salt, ivIters);
            byte[] hmacKey = CryptoUtils.pbkdf2(password, keyByteLength, salt, hmacKeyIters);
            byte[] aesKey = CryptoUtils.pbkdf2(password, keyByteLength, salt, aesKeyIters);

            hMac = Mac.getInstance("HmacSHA256", "BC");
            Key hMacKey = new SecretKeySpec(hmacKey, "HmacSHA256");
            hMac.init(hMacKey);

            Key aesSpec = new SecretKeySpec(aesKey,"AES");
            encryptingCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", new BouncyCastleProvider());
            encryptingCipher.init(Cipher.ENCRYPT_MODE, aesSpec, new IvParameterSpec(initVector));

            decryptingCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", new BouncyCastleProvider());
            decryptingCipher.init(Cipher.DECRYPT_MODE, aesSpec, new IvParameterSpec(initVector));
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Encrypts the byte array using a symmetric cipher.
     * Returns an encrypted byte array.
     * This should use the same internal cipher as decrypt() so anything encrypted by this
     * object will be decrypted into its original byte array
     * 
     * @param plainText : byte[]
     * @throws CryptoException 
     */
    @Override
    public byte[] encrypt(byte[] plainText) throws CryptoException  {
        try {
            byte[] plainTextWithSalt = CryptoUtils.appendSalt(plainText, numRandomBytes);
            // update hMac hash
            hMac.update(plainTextWithSalt);
            byte[] cipherText = new byte[encryptingCipher.getOutputSize(plainTextWithSalt.length + hMac.getMacLength())];
            int outputOffset = encryptingCipher.update(plainTextWithSalt, 0 , plainTextWithSalt.length, cipherText,0);

            // Add the hMac hash
            encryptingCipher.doFinal(hMac.doFinal(), 0, hMac.getMacLength(), cipherText, outputOffset);

            return cipherText;
        } catch (Exception e) {
            throw new CryptoException("Unable to encrypt the plain-text",e);
        }
    }

    /**
     * Decrypts the byte array using a symmetric cipher.
     * Returns the plain text version as byte array.
     * This should use the same internal cipher as encrypt() so anything encrypted by this
     * object will be decrypted into its original byte array
     * 
     * @param encryptedText : byte[]
     * @throws PasswordBasedCryptoException 
     */
    @Override
    public byte[] decrypt(byte[] cipherText) throws CryptoException {
        try {
            byte[] blob = decryptingCipher.doFinal(cipherText, 0, cipherText.length);
            int numBytes = blob.length - hMac.getMacLength();
            if (!isAuthentic(cipherText)) {
                throw new CryptoException("Cipher text is not authentic and may have been tampered with. Failed to decrypt");
            }
            byte[] plainTextAndHmac = CryptoUtils.removeSalt(blob,numRandomBytes);
            int plainTextLength = numBytes - numRandomBytes;
            byte[] plainText = ArrayUtils.subarray(plainTextAndHmac, 0, plainTextLength);

            return plainText;
        } catch (BadPaddingException bpe) {
            throw new CryptoException("Failed to decrypt cipher text. Cipher text is not authentic. Bad Padding",bpe);
        } catch (IllegalBlockSizeException ibse) {
            throw new CryptoException("Failed to decrypt cipher text. Cipher text is not authentic. Illegal Block Size",ibse);
        }
    }

    /**
     * Checks the cipher text against the current cipher for validity
     * 
     */
    public boolean isAuthentic(byte[] cipherText) {
        boolean isValid = false;
        try {
            byte[] blob = decryptingCipher.doFinal(cipherText, 0, cipherText.length);
            int numBytes = blob.length - hMac.getMacLength();
            hMac.update(blob, 0, numBytes);

            // Extract hMac hash and verify
            byte[] hMacHash = ArrayUtils.subarray(blob, numBytes, blob.length);
            if (MessageDigest.isEqual(hMac.doFinal(), hMacHash)) {
                isValid = true;
            }
        } catch (IllegalBlockSizeException e) {
            isValid = false;
        } catch (BadPaddingException e) {
            isValid = false;
        }
        return isValid;
    }
}