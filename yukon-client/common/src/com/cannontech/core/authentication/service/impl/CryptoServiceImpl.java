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
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.CryptoService;

public class CryptoServiceImpl implements CryptoService {
    private static final Base64 codec = new Base64();
    private static final String separator = System.getProperty("file.separator");
    private String keyFileDir = CtiUtilities.getYukonBase() + separator + "cache";
    private String keyFileName =  ".key2";
    private int keySize = 128; //SEE: Sun's JCE Unlimited Strength Jurisdiction Policy Files
    private Key key;
    private IvParameterSpec ivParameterSpec;
    
    {
        // the following is arbitrary and could be anything
        ivParameterSpec = new IvParameterSpec(new byte[]{32,96,-45,0,5,16,27,28,-54,-100,11,98,4,3,2,1});
        
    }
    
    public CryptoServiceImpl() {
        
    }
    
    @Override
    public String encrypt(final String input) throws GeneralSecurityException {
        byte[] raw = getEncryptCipher().doFinal(input.getBytes()); 
        byte[] encodedBytes = codec.encode(raw);
        String toString = new String(encodedBytes);
        return toString;
    }
    
    @Override
    public String decrypt(String input) throws GeneralSecurityException {
        byte[] raw = codec.decode(input.getBytes());
        byte[] output = getDecryptCipher().doFinal(raw);
        String toString = new String(output);
        return toString;
    }
    
    public Cipher getEncryptCipher() throws GeneralSecurityException {
        Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        encryptCipher.init(Cipher.ENCRYPT_MODE, getKey(), ivParameterSpec);
        return encryptCipher;
    }
    
    public Cipher getDecryptCipher() throws GeneralSecurityException {
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        decryptCipher.init(Cipher.DECRYPT_MODE, getKey(), ivParameterSpec);
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
            KeyGenerator generator = KeyGenerator.getInstance("AES");
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
