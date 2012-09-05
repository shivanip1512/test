package com.cannontech.encryption.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;

public class RSAPublicKeyCrypto {
    private static final int numRandomBytes = 15;
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    
    /**
     * Encrypts the byte array using a public key cipher.
     * Returns an encrypted byte array.
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
        } catch (NoSuchPaddingException e) {
            throw new CryptoException("Unable to encrypt plaintext. No such padding exists", e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Unable to encrypt plaintext. No such algorithm exists", e);
        } catch (InvalidKeyException e) {
            throw new CryptoException("Unable to encrypt plaintext. Key is invalidt", e);
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException("Unable to encrypt plaintext. Illegal Block Size", e);
        } catch (BadPaddingException e) {
            throw new CryptoException("Unable to encrypt plaintext. Bad Padding", e);
        }
        return cipherText;
    }

    /**
     * Decrypts the byte array using a public key cipher.
     * Returns the plain text version as byte array.
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
        } catch (NoSuchPaddingException e) {
            throw new CryptoException("Unable to decrypt cipher text. No such padding exists", e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Unable to decrypt cipher text. No such algorithm exists", e);
        } catch (InvalidKeyException e) {
            throw new CryptoException("Unable to decrypt cipher text. Key is invalidt", e);
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException("Unable to decrypt cipher text. Illegal Block Size", e);
        } catch (BadPaddingException e) {
            throw new CryptoException("Unable to decrypt cipher text. Bad Padding", e);
        }
        return decryptedText;
    }
    
}