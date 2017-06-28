package com.cannontech.system;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.jdom2.JDOMException;

import com.cannontech.encryption.ConfigCryptoUtils;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class GlobalSettingCryptoUtils extends ConfigCryptoUtils {

    /**
     * Decrypts the input string and returns the plain text value
     */
    public static String decryptValue(String encryptedText) throws CryptoException, DecoderException, IOException,
            JDOMException {
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(CryptoUtils.getSharedPasskey());
        String plainText = decryptValue(encryptedText, encrypter);
        return plainText;
    }

    /**
     * Encrypts the input string and returns the encrypted value
     */
    public static String encryptValue(String valuePlaintext) throws CryptoException, IOException, JDOMException {
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(CryptoUtils.getSharedPasskey());
        String encryptedValue = encryptValue(valuePlaintext, encrypter);
        return encryptedValue;
    }

}
