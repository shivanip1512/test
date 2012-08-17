package com.cannontech.encryption;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class MasterConfigCryptoUtils {

    private static final File masterCfgCryptoFile = new File(CtiUtilities.getKeysFolder(),"masterConfigKeyfile.dat");
    private static final String encryptionIndicator = "(AUTO_ENCRYPTED)";
    private final static Set<String> sensitiveData;
    
    static {
        sensitiveData = new HashSet<String>();
        sensitiveData.add("DB_USERNAME");
        sensitiveData.add("DB_PASSWORD");
        sensitiveData.add("DB_SQLSERVER");
        sensitiveData.add("DB_SQLSERVER_HOST");
        sensitiveData.add("DB_JAVA_URL");
    }

    private MasterConfigCryptoUtils() {/*Not instantiable. Utility class only */ }
    
    /**
     * Checks the sensitiveData set for this key
     * Returns true if its found
     */
    public static boolean isSensitiveData(String key) {
        return sensitiveData.contains(key);
    }

    /**
     * Returns the results of CryptoUtils.getPassKeyFromFile(masterCfgCryptoFile : File);
     * 
     * @return char[]
     * 
     * @throws JDOMException 
     * @throws CryptoException 
     * @throws IOException 
     */
    private static char[] getMasterCfgPasskey() throws IOException, CryptoException, JDOMException {
        return CryptoUtils.getPasskeyFromFile(masterCfgCryptoFile);
    }
    private static AESPasswordBasedCrypto encrypter;
    static {
        try {
            encrypter = new AESPasswordBasedCrypto(getMasterCfgPasskey());
        } catch (CryptoException e) {
            // Logging probably hasn't been set up...log to standard error.
            System.err.println("error creating encryptor");
            e.printStackTrace(System.err);
        } catch (IOException e) {
            // Logging probably hasn't been set up...log to standard error.
            System.err.println("error creating encryptor");
            e.printStackTrace(System.err);
        } catch (JDOMException e) {
            // Logging probably hasn't been set up...log to standard error.
            System.err.println("error creating encryptor");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Decrypts the input string and returns the plain text value
     * 
     * Decrypts the text following "(AUTO_ENCRYPTED)" in the string value
     * sent in. This will return the plain text as it was originally
     * 
     * @return valuePlainText : String
     * @throws CryptoException 
     */
    public static synchronized String decryptValue(String valueEncrypted) throws CryptoException {
        String valuePlainText = null;
        try {
            valueEncrypted = StringUtils.deleteWhitespace(valueEncrypted);
            valuePlainText = new String(encrypter.decrypt(Hex.decodeHex(valueEncrypted.substring(encryptionIndicator.length()).toCharArray())));
        } catch (DecoderException de) {
            throw new CryptoException(de);
        }
        return valuePlainText;
    }

    /**
     * Encrypts the input string and returns the encrypted value
     * 
     * The encrypted value will be encrypted using AES and will have
     * "(AUTO_ENCRYPTED)" appended to the value so the master.cfg parsers
     * can tell its encrypted. The rest of the value returned is the encrypted
     * data in hex format.
     * 
     * @return encryptedValue : String
     * @throws CryptoException 
     */
    public static synchronized String encryptValue(String valuePlaintext) throws CryptoException {
        valuePlaintext = StringUtils.deleteWhitespace(valuePlaintext);
        String encryptedValue =
                encryptionIndicator + new String(Hex.encodeHex(encrypter.encrypt(valuePlaintext.getBytes())));
        return encryptedValue;
    }

    /**
     *  Checks a value to determine if it is encrypted
     *  
     *  Checks for "(AUTO_ENCRYPTED)" to be the start of the value
     *  and returns true if this is found
     */
    public static boolean isEncrypted(String value) {
        value = StringUtils.deleteWhitespace(value);
        if (value.length() > encryptionIndicator.length() 
                && value.substring(0, encryptionIndicator.length()).equals(encryptionIndicator)) {
            return true;
        } else {
            return false;
        }
    }
}
