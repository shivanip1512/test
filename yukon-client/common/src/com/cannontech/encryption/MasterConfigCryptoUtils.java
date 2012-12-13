package com.cannontech.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;

import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class MasterConfigCryptoUtils {
    private static final File keysFolder = new File(BootstrapUtils.getKeysFolder());
    private static final File masterCfgCryptoFile = new File(keysFolder, "masterConfigKeyfile.dat");
    private static final File masterCfgCryptoLockFile = new File(keysFolder, "masterConfigKeyfile.lck");
    private static final String encryptionIndicator = "(AUTO_ENCRYPTED)";
    private static final Set<String> sensitiveData;
    private static AESPasswordBasedCrypto encrypter;
    
    static {
        try {
            encrypter = new AESPasswordBasedCrypto(getMasterCfgPasskey());
        } catch (CryptoException | IOException | JDOMException e) {
            // Logging probably hasn't been set up...log to standard error.
            System.err.println("error creating encryptor");
            e.printStackTrace(System.err);
        }
    }
    
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
        char[] passkey = null;

        if (!keysFolder.exists()) {
            keysFolder.mkdir();
        }
        FileOutputStream fos = new FileOutputStream(masterCfgCryptoLockFile);
        FileLock lock = fos.getChannel().lock();
        try {
            if (CryptoUtils.isValidCryptoFile(masterCfgCryptoFile)) {
                passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
            } else {
                masterCfgCryptoFile.delete();
                CryptoUtils.createNewCryptoFile(masterCfgCryptoFile);
                passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
            }
        } finally {
            lock.release();
            fos.close();
        }

        return passkey;
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
    public static String decryptValue(String valueEncrypted) throws CryptoException {
        String valuePlainText = null;
        try {
            valueEncrypted = StringUtils.deleteWhitespace(valueEncrypted);
            char[] value = valueEncrypted.substring(encryptionIndicator.length()).toCharArray();
            valuePlainText = new String(encrypter.decrypt(Hex.decodeHex(value)));
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
    public static String encryptValue(String valuePlaintext) throws CryptoException {
        valuePlaintext = StringUtils.deleteWhitespace(valuePlaintext);
        String encryptedValue =
                encryptionIndicator + new String(Hex.encodeHex(encrypter.encrypt(valuePlaintext.getBytes())));
        return encryptedValue;
    }

    /**
     *  Checks a value to determine if it is encrypted
     *  
     *  Checks for "(AUTO_ENCRYPTED)" to be the start of the value
     *  and returns true if this is found. This method is null safe and will
     *  return false if value is null
     *  
     *  @return true if value is encrypted, false if not encrypted or null
     */
    public static boolean isEncrypted(String value) {
        value = StringUtils.deleteWhitespace(value); // null safe
        if (StringUtils.isEmpty(value) // check for "" or Null
                || value.length() <= encryptionIndicator.length() // Ensure the next line doesn't throw indexOutOfBounds
                || !value.substring(0, encryptionIndicator.length()).equals(encryptionIndicator)) {
            return false;
        } else {
            return true;
        }
    }
}
