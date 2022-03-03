package com.cannontech.encryption;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.encryption.impl.AESEncryptedFileInputStream;
import com.cannontech.encryption.impl.AESEncryptedFileOutputStream;
import com.cannontech.tools.xml.SimpleXmlReader;
import com.cannontech.tools.xml.SimpleXmlWriter;

public class CryptoUtils {
    private static Logger log = YukonLogManager.getLogger(CryptoUtils.class);

    private static final int passKeyLength = 32; //chars
    private static final int rsaKeySize = 512; //4096 bits
    private static final String passkeyAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-+={}[];:,.?!@#$%^*()";

    /**
     * System-wide passkey to do encryption when no other passkey is available. Currently only using this to encrypt a file
     * which contains the actual passkey. In other words, this passkey unlocks the real passkey which is used for encryption.
     */
    private static final String yukonPasskey = "Bdk=5ohaIc51ifstd-zl2dCV)5iUE(DG";

    /**
     * System-wide salt for when no other salt is available.
     */
    private static final byte[] yukonSalt = {(byte)0x9B, (byte)0x02, (byte)0xF9, (byte)0x92,(byte)0x64, (byte)0xE5, (byte)0xE3, (byte)0x03,
        (byte)0xF2, (byte)0x9B, (byte)0x19, (byte)0x12,(byte)0x56, (byte)0x35, (byte)0x56, (byte)0x93};

    private static final String CRYPTO_FILE_VERSION = "1";
    private static final SecureRandom secureRandom = new SecureRandom();

    private CryptoUtils() {/*Not instantiable. Utility class only */ }

    /**
     * Generates a random password and returns it as a array of characters.
     * Uses a SecureRandom number generator and selects characters randomly
     * from passkeyAlphabet character array.
     * 
     * @param length : int length of generated passkey
     * @return passkey : char[]
     */
    public static char[] generateRandomPasskey(int length) {
        char [] passkey = new char[length];
        for (int i=0;i<length;i++) {
            int pos = secureRandom.nextInt(passkeyAlphabet.length());
            passkey[i] = passkeyAlphabet.charAt(pos);
        }
        return passkey;
    }

    /**
     * Returns a KeyPair object for use with RSA crypto of keysize rsaKeySize. 
     * 
     * @return key : KeyPair
     */
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(rsaKeySize*8);
        KeyPair key = keyGen.generateKeyPair();
        return key;
    }

    /**
     * Essentially a wrapper class for PBKDF2 function. (http://en.wikipedia.org/wiki/PBKDF2)
     * 
     * Applies a pseudorandom function, Hmac with SHA1 to the password along with a salt
     * repeatedly many times to produce a derived secret array of bytes. Can be used as a 
     * cryptographic key
     * 
     * @param password : char[] - password which is used to derive secret array of bytes
     * @param byteLength : int - byte array length returned
     * @param salt : byte[] - salt for hashing function
     * @param iterations : int - number of iterations to do salt/hash function
     * @return key : byte[] - random array of bytes derived from secret
     */
    public static byte[] pbkdf2(char[] password, int byteLength, byte[] salt, int iterations) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PBEKeySpec pbe = new PBEKeySpec(password,salt,iterations,byteLength*8);
        byte [] secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(pbe).getEncoded();
        return secret;
    }

    /**
     * Returns the byte array of random data. Size is determinted by numBytes int
     * @return randomBytes : byte[]
     */
    public static byte[] getRandomBytes(int numBytes) {
        byte[] randomBytes = new byte[numBytes];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    /**
     * Returns the predefinded and static yukon salt. Should only be used as a salt if no
     * other more secure random salt is available.
     */
    public static byte[] getYukonsalt() {
        return yukonSalt;
    }

    /**
     * Returns the results of getPassKey(sharedCryptoFile : File);
     */
    public static char[] getSharedPasskey() throws IOException, CryptoException, JDOMException {
        char[] passkey = null;

        File sharedCryptoFile = new File(BootstrapUtils.getKeysFolder().toFile(), "sharedKeyfile.dat");
        if (sharedCryptoFile.exists()) {
            passkey = CryptoUtils.getPasskeyFromCryptoFile(sharedCryptoFile);
        } else {
        	log.info(sharedCryptoFile.getName() + " doesn't exist. Creating new SharedCryptoFile.");
            CryptoUtils.createNewCryptoFile(sharedCryptoFile);
            passkey = CryptoUtils.getPasskeyFromCryptoFile(sharedCryptoFile);
        }

        return passkey;
    }

    /**
     * Returns the size in bytes of the RSA crypto key;
     * @return rsaKeySize : int;
     */
    public static int getRsaKeySize() {
        return rsaKeySize;
    }

    /**
     * Adds random bytes to beginning of data sent in by numBytes
     * @return data : byte[];
     */
    public static byte[] appendSalt(byte[] plainText, int numBytes) {
        return ArrayUtils.addAll(getRandomBytes(numBytes), plainText);
    }

    /**
     * Removes bytes from beginning of data sent in by numBytes
     * @return data : byte[];
     */
    public static byte[] removeSalt(byte[] plainText, int numBytes) {
        return ArrayUtils.subarray(plainText, numBytes, plainText.length);
    }

    /**
     * Creates a new 'cryptofile'
     * Formated in xml and encypted using AES.
     * Formating:
     *      <?xml version="1.0" encoding="UTF-8"?>
     *      <root>
     *          <version>1</version>
     *          <pk>...</pk>
     *      </root>
     *
     * The passkey generated is placed in <pk></pk> tag.
     */
    public static char[] createNewCryptoFile(File file) {
        if (file == null) { 
            return null; 
        }
        char[] passkey = null;
        try {
            passkey = generateRandomPasskey(passKeyLength);

            // File not found, Create a new one
            file.setReadOnly();
            Runtime.getRuntime().exec("attrib +H " + file.getPath()); // Set file hidden
            AESEncryptedFileOutputStream outputStream = new AESEncryptedFileOutputStream(file, yukonPasskey.toCharArray());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            SimpleXmlWriter xmlFile = new SimpleXmlWriter(writer);
            xmlFile.setRootElement(new Element("root"));
            xmlFile.createNewElementWithContent("version", CRYPTO_FILE_VERSION);
            xmlFile.setWorkingElementRoot();
            xmlFile.createNewElementWithContent("pk", passkey);
            xmlFile.writeAndClose();
        } catch (IOException e) {
        	log.error("Unable to save new passkey to file. Returning null passkey.");
            passkey = null;
        }

        return passkey;
    }

    /**
     * Returns the passkey stored in the file sent in.
     * The file sent in must be encrypted using yukonpasskey and be in the following format:
     *      <?xml version="1.0" encoding="UTF-8"?>
     *      <root>
     *          <version>1</version>
     *          <pk>...</pk>
     *      </root>
     * With the returned data being the data found between <pk></pk> elements.
     */
    public static char[] getPasskeyFromCryptoFile(File cryptoFile) throws IOException, CryptoException, JDOMException {
        char [] passkey = null;
        if (cryptoFile != null) {
            AESEncryptedFileInputStream inputStream = new AESEncryptedFileInputStream(cryptoFile, yukonPasskey.toCharArray());
            SimpleXmlReader xmlFile = new SimpleXmlReader(inputStream);
            passkey = xmlFile.getElementValue("pk").toCharArray();
        }
        return passkey;
    }
}
