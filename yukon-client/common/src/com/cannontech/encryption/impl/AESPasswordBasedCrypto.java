package com.cannontech.encryption.impl;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.PasswordBasedCrypto;

public class AESPasswordBasedCrypto implements PasswordBasedCrypto {
    private static Logger log = YukonLogManager.getLogger(AESPasswordBasedCrypto.class);

    private Cipher encryptingCipher;
    private Cipher decryptingCipher;
    private Mac hMac;
    
    private final byte[] salt;
    private final byte[] initVector;
    private final byte[] hmacKey;
    private final byte[] aesKey;


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
    
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String AUTHENTICATION_ALGORITHM = "HmacSHA256";
    
    /**
     * Calls AESPasswordBasedSymmetricCrypto(char[] password, int saltIters, int ivIters, int hmacKeyIters, int aesKeyIters)
     * with values saltIters=2005, ivIters=4019, hmacKeyIters=7003, aesKeyIters=10098.
     * 
     * @param password : char[] - password which is used to derive data used by the cipher
     * @throws CryptoException
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
     * @throws CryptoException
     */
    private AESPasswordBasedCrypto(char[] password, int saltIters, int ivIters, int hmacKeyIters, int aesKeyIters) throws CryptoException {
        try {
            Security.addProvider(new BouncyCastleFipsProvider());

            salt = CryptoUtils.pbkdf2(password, keyByteLength, CryptoUtils.getYukonsalt(), saltIters);
            initVector = CryptoUtils.pbkdf2(password, keyByteLength, salt, ivIters);
            hmacKey = CryptoUtils.pbkdf2(password, keyByteLength, salt, hmacKeyIters);
            aesKey = CryptoUtils.pbkdf2(password, keyByteLength, salt, aesKeyIters);

            reset();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /*
     * This sets up the hMac and Ciphers. When exceptions are thrown this needs to be re-executed.
     * This is due to the Cipher and hMac doFinal calls leaving them in a bad state after an exception
     */
    private void reset() throws CryptoException {
        try {
            hMac = Mac.getInstance(AUTHENTICATION_ALGORITHM, BouncyCastleFipsProvider.PROVIDER_NAME);
            Key hMacKey = new SecretKeySpec(hmacKey, AUTHENTICATION_ALGORITHM);
            hMac.init(hMacKey);

            Key aesSpec = new SecretKeySpec(aesKey,ENCRYPTION_ALGORITHM);
            encryptingCipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleFipsProvider());
            encryptingCipher.init(Cipher.ENCRYPT_MODE, aesSpec, new IvParameterSpec(initVector));

            decryptingCipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleFipsProvider());
            decryptingCipher.init(Cipher.DECRYPT_MODE, aesSpec, new IvParameterSpec(initVector));
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        
    }

	/**
	 * Encrypts the string returning a hex encoded string.
	 *
	 * Returns a UTF-8 hex encoded string
	 */
	@Override
	public synchronized String encryptToHexStr(String plainText) throws CryptoException {
		try {
			return new String(Hex.encodeHex(encrypt(plainText.getBytes("UTF-8"))));
		}  catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 is unknown", e);
		}
	}

	/**
	 * Decrypts the hex encoded string (UTF-8 bytes) returning a plain text string.
	 * 
	 * The hex string must be an even number of hex characters. If hexStr has an odd number or non hex characters
	 * a DecoderException will be thrown. 
	 */
	@Override
	public synchronized String decryptHexStr(String hexStr) throws CryptoException, DecoderException {
		try {
			return new String(decrypt(Hex.decodeHex(hexStr.toCharArray())), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 is unknown", e);
		}
	}

	/**
	 * Encrypts the byte array using a symmetric cipher.
	 * 
	 * Returns an encrypted byte array.
	 * This should use the same internal cipher as decrypt() so anything encrypted by this
	 * object will be decrypted into its original byte array
	 */
	@Override
	public synchronized byte[] encrypt(byte[] plainText) throws CryptoException  {
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
		    reset();
			throw new CryptoException("Unable to encrypt the plain-text", e);
		}
	}

	/**
	 * Decrypts the byte array using a symmetric cipher.
	 * 
	 * Returns the plain text version as byte array.
	 * This should use the same internal cipher as encrypt() so anything encrypted by this
	 * object will be decrypted into its original byte array
	 */
	@Override
	public synchronized byte[] decrypt(byte[] cipherText) throws CryptoException {
		try {
			verifyAuthenticity(cipherText);
			byte[] blob = decryptingCipher.doFinal(cipherText, 0, cipherText.length);
			int numBytes = blob.length - hMac.getMacLength();
			byte[] plainTextAndHmac = CryptoUtils.removeSalt(blob,numRandomBytes);
			int plainTextLength = numBytes - numRandomBytes;
			byte[] plainText = ArrayUtils.subarray(plainTextAndHmac, 0, plainTextLength);

			return plainText;
		} catch (BadPaddingException | IllegalBlockSizeException e) {
		    reset();
			throw new CryptoException("Failed to decrypt cipher text.", e);
		}
	}

	/**
	 * Checks the cipher text against the current cipher for validity
	 * Throws if not authentic
	 */
	public synchronized void verifyAuthenticity(byte[] cipherText) {
		try {
			byte[] blob = decryptingCipher.doFinal(cipherText, 0, cipherText.length);
			int numBytes = blob.length - hMac.getMacLength();
			hMac.update(blob, 0, numBytes);

			// Extract hMac hash and verify
			byte[] hMacHash = ArrayUtils.subarray(blob, numBytes, blob.length);
			if (!MessageDigest.isEqual(hMac.doFinal(), hMacHash)) {
				throw new CryptoException("CipherText is not authentic. Message Authentication Failed.");
			}
		} catch (IllegalBlockSizeException| BadPaddingException e) {
		    reset();
			throw new CryptoException("CipherText is not authentic. Decryption failed.", e);
		}
	}

	/**
	 * Checks the cipher text against the current cipher for validity
	 */
	public boolean isAuthentic(byte[] cipherText) {
		try {
			verifyAuthenticity(cipherText);
		} catch (CryptoException e) {
			log.info("CipherText not authentic. ", e);
			return false;
		}
		return true;
	}
}