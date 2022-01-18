package com.cannontech.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class YukonCryptoUtils {

    private static final Logger log = YukonLogManager.getLogger(YukonCryptoUtils.class);
    private static final String SECRET_KEY = "452C3BdAD-1RT2-508A-6D62-FDFB58B52TRM";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String CHARACTER_SET = "UTF-16";
    private static final String MESSAGEDIGEST_ALGORITHM = "SHA-256";
    private static final String ENCRYPTION_ALGORITHM = "AES";

    private static SecretKeySpec secretKey;
    private static Cipher cipher;

    /**
     * Set the SecretKeySpec by using the the provided secret key.
     */
    private static void setKey() {
        try {
            byte[] key = SECRET_KEY.getBytes(CHARACTER_SET);
            MessageDigest sha = MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM);
            key = sha.digest(key);
            secretKey = new SecretKeySpec(key, ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("Error while creating secret key ", e);
        }
    }

    /**
     * Set the Chiper by using provided mode(Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE) and SecretKeySpec.
     */
    private static void setChiper(Integer cipherMode) {
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            log.error("Error while initilizing cipher ", e);
        }
    }

    /**
     * Encrypt the input string and returns the encrypted value
     */
    public static String encrypt(String strToEncrypt)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        setKey();
        setChiper(Cipher.ENCRYPT_MODE);
        return new String(Base64.getEncoder().encode(cipher.doFinal(strToEncrypt.getBytes(CHARACTER_SET))));
    }

    /**
     * Decrypt the input string and returns the plain text value
     */
    public static String decrypt(String strToDecrypt)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        setKey();
        setChiper(Cipher.DECRYPT_MODE);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), CHARACTER_SET);
    }
}