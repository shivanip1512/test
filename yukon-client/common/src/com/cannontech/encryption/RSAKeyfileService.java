package com.cannontech.encryption;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.joda.time.Instant;

public interface RSAKeyfileService {
    
    public void generateNewKeyPair() throws CryptoException;
    
    /**
     * Creates a file containing the plainTextData encrypted using RSA with the public key.
     */
    public void createFile(File file, byte[] plainTextData, byte[] publicKey) throws CryptoException;
    
    public PublicKey getPublicKey() throws CryptoException;
    
    public byte[] decryptAndExtractData(File file) throws CryptoException;
    
    public Instant getExpiration();
    
    boolean doesKeyPairExist();
    
    boolean isKeyPairExpired();

    /**
     * Creates a public key String from the given private Key.
     */
    String getPublicKeyFromPrivateKey(String privateKeyString) throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException;
}
