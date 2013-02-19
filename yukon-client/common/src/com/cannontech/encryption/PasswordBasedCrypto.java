package com.cannontech.encryption;

import org.apache.commons.codec.DecoderException;

public interface PasswordBasedCrypto {
    public byte[] encrypt(byte[] plainText) throws CryptoException;
    public byte[] decrypt(byte[] encryptedText) throws CryptoException;
    public String encryptToHexStr(String plainText) throws CryptoException;
    public String decryptHexStr(String hexStr) throws CryptoException, DecoderException;
}