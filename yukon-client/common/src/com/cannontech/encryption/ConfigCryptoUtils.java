package com.cannontech.encryption;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public abstract class ConfigCryptoUtils {

    private static final String encryptionDesignation = "(AUTO_ENCRYPTED)";

    /**
     * Decrypts the input string and returns the plain text value
     * 
     * Decrypts the text following "(AUTO_ENCRYPTED)" in the string value
     * sent in. This will return the plain text as it was originally
     */
    protected static String decryptValue(String encryptedText, AESPasswordBasedCrypto encrypter) throws CryptoException, DecoderException {
        String plainText = null;
        encryptedText = StringUtils.deleteWhitespace(encryptedText);
        String hexStr = encryptedText.substring(encryptionDesignation.length());
        plainText = encrypter.decryptHexStr(hexStr);
        return plainText;
    }
    

    /**
     * Encrypts the input string and returns the encrypted value
     * 
     * The encrypted value will be encrypted using AES and will have
     * "(AUTO_ENCRYPTED)" appended to the value to mark it as encrypted.
     * The rest of the value returned is the encrypted
     * data in hex format.
     */
    protected static String encryptValue(String valuePlaintext, AESPasswordBasedCrypto encrypter) throws CryptoException {
        valuePlaintext = StringUtils.deleteWhitespace(valuePlaintext);
        String encryptedValue = encryptionDesignation + encrypter.encryptToHexStr(valuePlaintext);
        return encryptedValue;
    }
    
    /**
     * Checks a value to determine if it is encrypted
     * 
     * Checks for "(AUTO_ENCRYPTED)" to be the start of the value
     * and returns true if this is found. This method is null safe and will
     * return false if value is null
     * 
     * @return true if value is encrypted, false if not encrypted or null
     */
    public static boolean isEncrypted(String value) {
        value = StringUtils.deleteWhitespace(value);
        if (StringUtils.isEmpty(value) || value.length() <= encryptionDesignation.length()
            || !value.substring(0, encryptionDesignation.length()).equals(encryptionDesignation)) {
            return false;
        } else {
            return true;
        }
    }
}
