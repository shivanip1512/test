package com.cannontech.encryption;

import java.io.File;
import java.security.PublicKey;

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
}
