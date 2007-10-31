package com.cannontech.core.authentication.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.CryptoService;

public class CryptoServiceImpl implements CryptoService {
    private static final Base64 base64Encoder = new Base64();
    private static final String separator = System.getProperty("file.separator");
    private String keyFileDir = CtiUtilities.getYukonBase() + separator + "cache";
    private String keyFileName =  ".key";
    private String algorithm = "Blowfish";
    private int keySize = 128; //SEE: Sun's JCE Unlimited Strength Jurisdiction Policy Files
    private Key key;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    
    public CryptoServiceImpl() {
    
    }
    
    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    public void setKeyFileName(final String keyFileName) {
        this.keyFileName = keyFileName;
    }
    
    public void setKeySize(final int keySize) {
        this.keySize = keySize;
    }
    
    public String encrypt(final String input) throws GeneralSecurityException {
        byte[] raw = getEncryptCipher().doFinal(input.getBytes()); 
        byte[] base64Output = base64Encoder.encode(raw);
        String toString = new String(base64Output);
        return toString;
    }
    
    public String decrypt(String input) throws GeneralSecurityException {
        byte[] raw = base64Encoder.decode(input.getBytes());
        byte[] output = getDecryptCipher().doFinal(raw);
        String toString = new String(output);
        return toString;
    }
    
    public synchronized Cipher getEncryptCipher() throws GeneralSecurityException {
        if (this.encryptCipher == null) {
            this.encryptCipher = Cipher.getInstance(algorithm);
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, getKey());
        }
        return encryptCipher;
    }
    
    public synchronized Cipher getDecryptCipher() throws GeneralSecurityException {
        if (this.decryptCipher == null) {
            this.decryptCipher = Cipher.getInstance(algorithm);
            this.decryptCipher.init(Cipher.DECRYPT_MODE, getKey());
        }
        return decryptCipher;
    }
    
    private synchronized Key getKey() {
        if (key == null) {
            try {
                key = loadKey();
            } catch (IOException generateNewKey) {
                key = generateKey();
                saveKey();
            } catch (ClassNotFoundException ex2) {
                throw new RuntimeException(ex2);
            }
        }
        return key;
    }
    
    private synchronized Key loadKey() throws IOException, ClassNotFoundException {
        final String fileName = this.keyFileDir + separator + this.keyFileName;
        
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(fileName));
            Key loadedKey = (Key) ois.readObject();
            return loadedKey;
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (IOException ignore) {}    
        }
    }
    
    private synchronized Key generateKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(algorithm);
            generator.init(keySize);
            Key generatedKey = generator.generateKey();
            return generatedKey;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    private synchronized void saveKey() {
        File dir = new File(this.keyFileDir);
        if (!dir.exists()) dir.mkdir();
        
        final String fileName = this.keyFileDir + separator + this.keyFileName;
        
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {}
            }
        }
    }
    
    
    
}
