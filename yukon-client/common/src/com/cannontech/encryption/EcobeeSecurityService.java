package com.cannontech.encryption;

import java.io.InputStream;
import java.util.NoSuchElementException;

import org.joda.time.Instant;

import com.cannontech.common.exception.EcobeePGPException;

public interface EcobeeSecurityService {

    /**
     * This method generate the PGP private and public key for ecobee runtime job
     * file encryption and decryption. In order to generate PGP Key pair an
     * PGPKeyRingGenerator is created and from this we generate the public and private key
     * and return the creation date.
     */
    Instant generateEcobeePGPKeyPair() throws EcobeePGPException;

    /**
     * This method will return the PGP public key if present in database.
     * If an PGP key pair is not present then generate an new PGP key pair
     * save to database and return the PGP public key.
     */
    String getEcobeePGPPublicKey() throws Exception;

    /**
     * This method is used to decrypt the encrypted gpg file received from Ecobee.
     * To decrypt the gpg file we will be using the saved PGP private key.
     * In case No key is found we will throw an exception.
     */
    byte[] decryptEcobeeFile(InputStream imputStream) throws EcobeePGPException;

    /**
     * This method will return the Ecobee Key Pair creation time.
     * If no Key Pair is present it will throw an Exception.
     */
    Instant getEcobeeKeyPairCreationTime() throws NoSuchElementException;
}
