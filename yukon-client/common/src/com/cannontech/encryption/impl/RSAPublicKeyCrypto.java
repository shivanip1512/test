package com.cannontech.encryption.impl;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;

public class RSAPublicKeyCrypto {
    private static final int numRandomBytes = 15;
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    
    /**
     * Encrypts the byte array using a public key cipher.
     * Returns an encrypted byte array.
     * This should use the same internal cipher as decrypt() so anything encrypted by this
     * object will be decrypted into its original byte array
     * 
     * @param plainText : byte[], publicKey PublicKey
     * @throws CryptoException 
     */
    public static byte[] encrypt(byte[] plainText, PublicKey publicKey) throws CryptoException  {
        byte[] cipherText = null;
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            plainText = CryptoUtils.appendSalt(plainText, numRandomBytes);
            cipherText = cipher.doFinal(plainText);
        } catch (Exception e) {
            throw new CryptoException("Unable to encrypt plainText", e);
        }
        return cipherText;
    }

    /**
     * Decrypts the byte array using a public key cipher.
     * Returns the plain text version as byte array.
     * This should use the same internal cipher as encrypt() so anything encrypted by this
     * object will be decrypted into its original byte array
     * 
     * @param encryptedText : byte[], privatekey PrivateKey
     * @throws CryptoException 
     */
    public static byte[] decrypt(byte[] cipherText, PrivateKey privateKey) throws CryptoException {
        byte[] decryptedText = null;
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedText = cipher.doFinal(cipherText);
            decryptedText = CryptoUtils.removeSalt(decryptedText, numRandomBytes);
        } catch (Exception e) {
            throw new CryptoException("caught exception in encrypt", e);
        }
        return decryptedText;
    }
    
}