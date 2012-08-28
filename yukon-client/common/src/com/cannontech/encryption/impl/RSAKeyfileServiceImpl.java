package com.cannontech.encryption.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.RSAKeyfileService;

public class RSAKeyfileServiceImpl implements RSAKeyfileService {
    private final int daysToExpire = 2;
    private final int numStartBytes = 151;
    private static final String ALGORITHM = "RSA";
    
    private KeyPair keyPair = null;
    private Instant expires = null;
    
    @Override
    public void generateNewKeyPair() throws CryptoException {
        try {
            keyPair = CryptoUtils.generateRSAKeyPair();
            expires = new Instant().plus(Duration.standardDays(daysToExpire));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("caught exception in generateNewKeyPair", e);
        }
    }

    @Override
    public PublicKey getPublicKey() throws CryptoException {
        if (!doesKeyPairExist()) {
            return null;
        }
        return keyPair.getPublic();
    }

    @Override
    public boolean doesKeyPairExist() {
        return keyPair != null;
    }

    @Override
    public boolean isKeyPairExpired() {
        return expires == null || expires.isBeforeNow();
    }

    /**
     * Returns the byte array key from the file.
     * 
     * Opens an inputstream of the file sent in. Ignores the first randomDataByteLength
     * bytes of data and reads the next 128 bytes, discarding any following data.
     * Uses the current private key and RsaPublicKeyCrypto.decrypt() to decrypt the 
     * data. Throws exception if the private key has expired or doesn't exist. 
     * 
     * @param file : File
     * @return decryptedBytes : byte[]
     * @throws PublicKeyCryptoException
     * @throws CryptoException 
     */
    @Override
    public byte[] decryptAndExtractData(File file) throws CryptoException {
        if (!doesKeyPairExist() || isKeyPairExpired()) {
            throw new CryptoException("Public and private key have expired or have not been created yet.");
        }
        byte[] buffer = new byte[numStartBytes+CryptoUtils.getRsaKeySize()];
        byte[] decryptedBytes;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(buffer);
            byte[] encryptedBytes = Arrays.copyOfRange(buffer, numStartBytes, numStartBytes+CryptoUtils.getRsaKeySize());
            decryptedBytes = RSAPublicKeyCrypto.decrypt(encryptedBytes, keyPair.getPrivate());
            inputStream.close();
        } catch (FileNotFoundException e) {
            throw new CryptoException(e);
        } catch (IOException e) {
            throw new CryptoException(e);
        }
        return decryptedBytes;
    }

    @Override
    public void createFile(File file, byte[] plainTextData, byte[] publicKeyBytes) throws CryptoException {
        int randomDataEndPaddingSize = 50000;
        try {
            PublicKey publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            int endPaddingLength = new SecureRandom().nextInt(randomDataEndPaddingSize);

            // Generating Random Data
            byte[] startPaddingBytes = CryptoUtils.getRandomBytes(numStartBytes);
            byte[] endPaddingBytes = CryptoUtils.getRandomBytes(endPaddingLength);

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] encryptedBytes = RSAPublicKeyCrypto.encrypt(plainTextData, publicKey);
            outputStream.write(startPaddingBytes);
            outputStream.write(encryptedBytes);
            outputStream.write(endPaddingBytes);
            outputStream.close();
        } catch (Exception e) {
            throw new CryptoException(e);
        } 
    }

    @Override
    public Instant getExpiration() {
        if (expires == null) {
            return new Instant();
        } else {
            return expires;
        }
    }
}
