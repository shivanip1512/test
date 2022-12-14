package com.cannontech.encryption.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.RSAKeyfileService;
import com.cannontech.encryption.SecurityKeyPair;

public class RSAKeyfileServiceImpl implements RSAKeyfileService {
    private final int daysToExpire = 7;
    private final int numStartBytes = 151;
    private static final String ALGORITHM = "RSA";
    
    private KeyPair keyPair = null;
    private Instant expires = null;
    
    @Override
    public void generateNewKeyPair() throws CryptoException {
        try {
            keyPair = CryptoUtils.generateRSAKeyPair();
            expires = Instant.now().plus(Duration.standardDays(daysToExpire));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("RSA Algorithm is not available", e);
        }
    }

    @Override
    public PublicKey getPublicKey() {
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
        byte[] buffer = new byte[numStartBytes + CryptoUtils.getRsaKeySize()];
        byte[] decryptedBytes = null;

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            inputStream.read(buffer);
            byte[] encryptedBytes = Arrays.copyOfRange(buffer, numStartBytes, numStartBytes+CryptoUtils.getRsaKeySize());
            decryptedBytes = RSAPublicKeyCrypto.decrypt(encryptedBytes, keyPair.getPrivate());
        }  catch (IOException e) {
            throw new CryptoException("IO exception reading data from Keyfile",e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
        
        return decryptedBytes;
    }

    /**
     * Creates a file containing the plainTextData encrypted using RSA with the public key.
     * 
     * Writes numStartBytes number of random bytes to the beggining of the file.
     * Then encrypts the plainTextData and writes that to the file after the random data.
     * Then writes a random number of random bytes to the tail of the file.
     * 
     * @param file - file to write to
     * @param plainTextData - data to encrypt and write in file
     * @return publicKeyBytes - public key to use for encryption
     * @throws CryptoException 
     */
    @Override
    public void createFile(File file, byte[] plainTextData, byte[] publicKeyBytes) throws CryptoException {
        int randomDataEndPaddingSize = 50000;
        FileOutputStream outputStream = null;
        try {
            PublicKey publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            int endPaddingLength = new SecureRandom().nextInt(randomDataEndPaddingSize);

            // Generating Random Data
            byte[] startPaddingBytes = CryptoUtils.getRandomBytes(numStartBytes);
            byte[] endPaddingBytes = CryptoUtils.getRandomBytes(endPaddingLength);

            outputStream = new FileOutputStream(file);
            byte[] encryptedBytes = RSAPublicKeyCrypto.encrypt(plainTextData, publicKey);
            outputStream.write(startPaddingBytes);
            outputStream.write(encryptedBytes);
            outputStream.write(endPaddingBytes);
        } catch (IOException e) {
            throw new CryptoException(e);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {}
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
    
    @Override
    public SecurityKeyPair getKeyPair(String keyString)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Security.addProvider(new BouncyCastleFipsProvider());
        
        BufferedReader bufferedReader = new BufferedReader(new StringReader(keyString));
        PEMParser pemParser = new PEMParser(bufferedReader);
        PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
        KeyPair keyPair = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
        pemParser.close();
        PrivateKey privateKey = keyPair.getPrivate();
        
        byte privateKeyEncoded[] = privateKey.getEncoded();
        String privateKeyString = new String(Base64.encodeBase64(privateKeyEncoded));
        byte publicKeyEncoded[] = keyPair.getPublic().getEncoded();
        String publicKeyString = new String(Base64.encodeBase64(publicKeyEncoded));
        
        return new SecurityKeyPair(privateKeyString, publicKeyString);
    }
}
