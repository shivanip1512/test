package com.cannontech.encryption;

public interface PasswordBasedCrypto {
    public byte[] encrypt(byte[] plainText) throws CryptoException;
    public byte[] decrypt(byte[] encryptedText) throws CryptoException;
}
