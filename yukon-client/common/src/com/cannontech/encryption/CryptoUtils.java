package com.cannontech.encryption;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.encryption.impl.AESEncryptedFileInputStream;
import com.cannontech.encryption.impl.AESEncryptedFileOutputStream;
import com.cannontech.tools.xml.SimpleXmlReader;
import com.cannontech.tools.xml.SimpleXmlWriter;

public class CryptoUtils {
    private static Logger log = YukonLogManager.getLogger(CryptoUtils.class);
    private static final int passKeyLength = 32; //chars
    private static final int rsaKeySize = 512; //512 = 4096 bits
    private static final File keysFolder = new File(CtiUtilities.getKeysFolder());
    private static final File masterCfgCryptoFile = new File(keysFolder,"masterConfigKeyfile.dat");
    private static final File sharedCryptoFile = new File(keysFolder,"sharedKeyfile.dat");
    private static final String passkeyAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"  
        + "0123456789_-+={}[];:,.?!@#$%^*()";
    private static final char[] yukonPasskey = {'B','d','k','=','5','o','h','a','I','c','5','1','i','f','s','t'
        ,'d','-','z','l','2','d','C','V',')','5','i','U','E','(','D','G'};
    private static final byte[] yukonSalt = {(byte)0x9B, (byte)0x02, (byte)0xF9, (byte)0x92,(byte)0x64, (byte)0xE5, (byte)0xE3, (byte)0x03,
        (byte)0xF2, (byte)0x9B, (byte)0x19, (byte)0x12,(byte)0x56, (byte)0x35, (byte)0x56, (byte)0x93};
    private static SecureRandom secureRandom = new SecureRandom();

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
     * @throws NoSuchAlgorithmException
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
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
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
     * @return yukonSalt : byte[]
     */
    public static byte[] getYukonsalt() {
        return yukonSalt;
    }

    /**
     * Returns the results of getPassKey(masterCfgCryptoFile : File);
     * @return char[]
     * @throws JDOMException 
     * @throws CryptoAuthenticationException 
     * @throws PasswordBasedCryptoException 
     * @throws IOException 
     */
    public static char[] getMasterCfgPasskey() throws IOException, PasswordBasedCryptoException, CryptoAuthenticationException, JDOMException {
        return getPasskey(masterCfgCryptoFile);
    }

    /**
     * Returns the results of getPassKey(sharedCryptoFile : File);
     * @return char[]
     * @throws JDOMException 
     * @throws CryptoAuthenticationException 
     * @throws PasswordBasedCryptoException 
     * @throws IOException 
     */
    public static char[] getSharedPasskey() throws IOException, PasswordBasedCryptoException, CryptoAuthenticationException, JDOMException {
        return getPasskey(sharedCryptoFile);
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
     * Formated in xml and encypted using AES with yukonPasskey sent in.
     * Formating:
     *      <?xml version="1.0" encoding="UTF-8"?>
     *      <root>
     *          <version>1</version>
     *          <pk>...</pk>
     *      </root>
     *
     * The passkey sent in is placed in <pk></pk> tag.
     * 
     */
    private static void createNewCryptoFile(File file, char[] passkey) {
        try {
            if (!keysFolder.exists()) {
                keysFolder.mkdir();
            }
            // File not found, Create a new one!
            file.setReadOnly();
            Runtime.getRuntime().exec("attrib +H " + file.getPath()); // Set file hidden

            AESEncryptedFileOutputStream outputStream = new AESEncryptedFileOutputStream(file, yukonPasskey);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            SimpleXmlWriter xmlFile = new SimpleXmlWriter(writer);
            xmlFile.setRootElement(new Element("root"));
            xmlFile.createNewElementWithContent("version", "1");
            xmlFile.setWorkingElementRoot();
            xmlFile.createNewElementWithContent("pk", passkey);
            xmlFile.writeAndClose();
        } catch (IOException e) {
            log.warn("Unable to save new passkey to file. Returning null", e);
            passkey = null;
        } catch (PasswordBasedCryptoException e) {
            log.warn("caught exception in createNewCryptoFile", e);
            passkey = null;
        } catch (CryptoAuthenticationException e) {
            log.warn("caught exception in createNewCryptoFile", e);
        }
    }

    /**
     * Returns the passkey stored in the file sent in.
     * The file sent in must be encrypted using yukonpasskey and be in the following format:
     *      <?xml version="1.0" encoding="UTF-8"?>
     *      <root>
     *          <version>1</version>
     *          <pk>...</pk>
     *      </root>
     * With the data found between <pk></pk> being returned as a character array.
     * @throws CryptoAuthenticationException 
     * @throws PasswordBasedCryptoException 
     * @throws IOException 
     * @throws JDOMException 
     * 
     */
    private static char[] getPasskey(File cryptoFile) throws IOException, PasswordBasedCryptoException, CryptoAuthenticationException, JDOMException {
        char [] passkey = null;
        try {
            AESEncryptedFileInputStream inputStream = new AESEncryptedFileInputStream(cryptoFile, yukonPasskey);
            SimpleXmlReader xmlFile = new SimpleXmlReader(inputStream);
            passkey = xmlFile.getElementValue("pk").toCharArray();
        } catch (FileNotFoundException fnfe) {
            passkey = generateRandomPasskey(passKeyLength);
            createNewCryptoFile(cryptoFile, passkey);
        }

        return passkey;
    }
}
