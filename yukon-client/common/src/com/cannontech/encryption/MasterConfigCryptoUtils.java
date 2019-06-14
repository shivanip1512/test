package com.cannontech.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

public class MasterConfigCryptoUtils extends ConfigCryptoUtils {
    private static Logger log = YukonLogManager.getLogger(MasterConfigCryptoUtils.class);

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

        try (FileOutputStream fos = new FileOutputStream(masterCfgCryptoLockFile); FileLock lock = fos.getChannel().lock()) {
            try {
                if (masterCfgCryptoFile.exists()) {
                    passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
                } else {
                	log.info(masterCfgCryptoFile.getName() + " doesn't exist. Creating a new CryptoFile for master.cfg");
                    CryptoUtils.createNewCryptoFile(masterCfgCryptoFile);
                    passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
                }
            } catch (Exception e) {
                log.info(e);
            }
        } catch (Exception e) {
            log.info("Failed to lock lock file");
            try {
                if (masterCfgCryptoFile.exists()) {
                    passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
                    log.info("Successfully read in passkey after failing to lock lockfile");
                }
            } catch(Exception x) {
                log.info(x);
            }
        }

        return passkey;
    }

    /**
     * Decrypts the input string and returns the plain text value
     */
    public static String decryptValue(String encryptedText) throws CryptoException {
        String plainText = null;
        try {
            plainText = decryptValue(encryptedText, encrypter);
        } catch (DecoderException | CryptoException e) {
        	throw new CryptoException("Unable to decrypt master.cfg encrypted value.", e);
        }
        return plainText;
    }

    /**
     * Encrypts the input string and returns the encrypted value
     */
    public static String encryptValue(String valuePlaintext) throws CryptoException {
        String encryptedValue = encryptValue(valuePlaintext, encrypter);
        return encryptedValue;
    }
}
