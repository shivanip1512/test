package com.cannontech.encryption.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cannontech.encryption.CryptoException;

public class AESEncryptedFileOutputStream extends ByteArrayOutputStream  {

    private FileOutputStream fileOutputStream;
    private AESPasswordBasedCrypto aes;
    
    /**
     * A Wrapper object for ByteArrayOutputStream.
     * 
     * Keeps all data written to the stream in a byte array as ByteArrayOutputStream.
     * Encryption is done when close(). Not closing this stream will result in no
     * data being written to the file.
     * 
     * @param file : File - AES encrypted file
     * @param password : char[] - password used to decrypt the file
     * 
     * @throws IOException
     * @throws PasswordBasedCryptoException
     * @throws CryptoException
     */
    public AESEncryptedFileOutputStream(File file, char[] password) throws IOException, CryptoException {
        super();
        this.aes = new AESPasswordBasedCrypto(password);
        fileOutputStream = new FileOutputStream(file);
    }

    public void close() throws IOException {
        try {
            byte[] bytes = toByteArray();
            fileOutputStream.write(aes.encrypt(bytes));
        } catch (CryptoException e) {
            throw new IOException("Failed to encrypt input to stream.",e);
        }
        fileOutputStream.close();
    }

}
