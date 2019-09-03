package com.cannontech.encryption;

import java.util.NoSuchElementException;

import org.joda.time.Instant;


public interface ItronSecurityService {

    /**
     * This method generate the SSH RSA private and public key for Itron.
     * The private key is encrypted depending on if a password is specified.
     * This returns the creation date.
     */
    Instant generateItronSshRsaPublicPrivateKeys(String comment, String passPhrase) throws Exception;

    /**
     * This method creates a basic ItronSecuurityKeyPair object with the keys in String format. If the private key
     * has encryption, it will attempt to decrypt it with the global PW.
     * This returns the keyPair
     */
    ItronSecurityKeyPair getItronSshRsaKeyPair() throws Exception;

    /**
     * This method will return the Itron Public key as a String.
     * @throws Exception 
     */
    String getItronPublicSshRsaKey() throws Exception;
    
    /**
     * This method will return the Itron Private key as a String.
     * If encrypted and a passPhrase is provided it will attempt
     *  to decrypt before returning
     * @throws Exception 
     */
    String getItronPrivateSshRsaKey() throws Exception;

    /**
     * This method will return the Itron Key Pair creation time.
     * If no Key Pair is present it will throw an Exception.
     */
    Instant getItronKeyPairCreationTime() throws NoSuchElementException;
}
