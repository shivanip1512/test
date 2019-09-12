package com.cannontech.encryption;

import java.util.NoSuchElementException;

import org.joda.time.Instant;

import com.cannontech.encryption.impl.ItronSecurityException;


public interface ItronSecurityService {

    /**
     * This method generate the SSH RSA private and public key for Itron.
     * The private key is encrypted depending on if a password is specified.
     * Password is optional and can be specified in Admin Configuration
     * This returns the creation date.
     */
    Instant generateItronSshRsaPublicPrivateKeys(String comment) throws ItronSecurityException;

    /**
     * This method creates a basic ItronSecuurityKeyPair object with the keys in String format. If the private key
     * has encryption, it will attempt to decrypt it with the global PW.
     * This returns the keyPair
     */
    ItronSecurityKeyPair getItronSshRsaKeyPair() throws ItronSecurityException;

    /**
     * This method will return the Itron Public key as a String.
     * @throws Exception 
     */
    String getItronPublicSshRsaKey() throws ItronSecurityException;
    
    /**
     * This method will return the Itron Private key as a String.
     * If encrypted and a passPhrase is provided it will attempt
     *  to decrypt before returning
     * @throws Exception 
     */
    String getItronPrivateSshRsaKey() throws ItronSecurityException;

    /**
     * This method will return the Itron Key Pair creation time.
     * If no Key Pair is present it will throw an Exception.
     */
    Instant getItronKeyPairCreationTime() throws NoSuchElementException;

    /**
     * This method takes a string for a privateKey and encrypts it if a password is provided.
     * This returns the privateKey encrypted with the privateKeyPassword 
     * or just the privateKey if no password is specified
     * @throws Exception 
     */
    String encryptPrivateKey(String privateKey) throws ItronSecurityException;
}
