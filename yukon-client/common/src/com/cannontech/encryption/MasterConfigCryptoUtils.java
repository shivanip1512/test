package com.cannontech.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class MasterConfigCryptoUtils {
    private static Logger log = YukonLogManager.getLogger(MasterConfigCryptoUtils.class);

    private static final String encryptionDesignation = "(AUTO_ENCRYPTED)";
    private static AESPasswordBasedCrypto encrypter;

    static {
        try {
            encrypter = new AESPasswordBasedCrypto(getMasterCfgPasskey());
        } catch (CryptoException | IOException | JDOMException e) {
        	throw new IllegalStateException("Corrupt or invalid crypto file for master.cfg found. " +
            			"If this file has been tampered with it will need to be removed and a new one will be generated. " +
            			"Settings in master.cfg will need to be plain text for the new file to re-encrypt properly.",e);
        }
    }

    private MasterConfigCryptoUtils() {/*Not instantiable. Utility class only */ }

    /**
     * Returns the results of CryptoUtils.getPassKeyFromFile(masterCfgCryptoFile : File);
     * 
     * @return char[]
     */
    private static char[] getMasterCfgPasskey() throws IOException, CryptoException, JDOMException {
        char[] passkey = null;

        File masterCfgCryptoFile = new File(BootstrapUtils.getKeysFolder().toFile(), "masterConfigKeyfile.dat");
        File masterCfgCryptoLockFile = new File(BootstrapUtils.getKeysFolder().toFile(), "masterConfigKeyfile.lck");

        try (FileOutputStream fos = new FileOutputStream(masterCfgCryptoLockFile);
    		 FileLock lock = fos.getChannel().lock()) {
            if (masterCfgCryptoFile.exists()) {
                passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
            } else {
            	log.info(masterCfgCryptoFile.getName() + " doesn't exist. Creating a new CryptoFile for master.cfg");
                CryptoUtils.createNewCryptoFile(masterCfgCryptoFile);
                passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
            }
        }

        return passkey;
    }

    /**
     * Decrypts the input string and returns the plain text value
     * 
     * Decrypts the text following "(AUTO_ENCRYPTED)" in the string value
     * sent in. This will return the plain text as it was originally
     * 
     */
    public static String decryptValue(String encryptedText) throws CryptoException {
        String plainText = null;
        try {
            encryptedText = StringUtils.deleteWhitespace(encryptedText);
            String hexStr = encryptedText.substring(encryptionDesignation.length());
            plainText = encrypter.decryptHexStr(hexStr);
        } catch (DecoderException | CryptoException e) {
        	throw new CryptoException("Unable to decrypt master.cfg encrypted value.", e);
        }
        return plainText;
    }

    /**
     * Encrypts the input string and returns the encrypted value
     * 
     * The encrypted value will be encrypted using AES and will have
     * "(AUTO_ENCRYPTED)" appended to the value so the master.cfg parsers
     * can tell its encrypted. The rest of the value returned is the encrypted
     * data in hex format.
     */
    public static String encryptValue(String valuePlaintext) throws CryptoException {
        valuePlaintext = StringUtils.deleteWhitespace(valuePlaintext);
        String encryptedValue = encryptionDesignation + encrypter.encryptToHexStr(valuePlaintext);
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
                || value.length() <= encryptionDesignation.length() // Ensure the next line doesn't throw indexOutOfBounds
                || !value.substring(0, encryptionDesignation.length()).equals(encryptionDesignation)) {
            return false;
        } else {
            return true;
        }
    }
}
