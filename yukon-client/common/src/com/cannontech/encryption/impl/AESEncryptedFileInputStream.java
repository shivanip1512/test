package com.cannontech.encryption.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.cannontech.encryption.CryptoException;

public class AESEncryptedFileInputStream extends ByteArrayInputStream {
    
    /**
     * A wrapper object for ByteArrayInputStream.
     * 
     * Loads the entire file in memory then decrypts the data before initializing
     * the underlying ByteArrayInputStream.
     * 
     * @param file : File - AES encrypted file
     * @param password : char[] - password used to decrypt the file
     * 
     * @throws IOException
     * @throws CryptoException
     */
    public AESEncryptedFileInputStream(File file, char[] password) throws IOException, CryptoException {
        super(getBuffer(file, password));
    }

    // Gets a decrypted byte array buffer to send the underlying ByteArrayInputStream
    private static byte[] getBuffer(File file, char[] password) throws IOException, CryptoException {
        InputStream is = new FileInputStream(file);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            is.close();
            throw new IOException("File is too large.");
        }

        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file "+file.getName());
        }
        is.close();

        return new AESPasswordBasedCrypto(password).decrypt(bytes);
    }
}
