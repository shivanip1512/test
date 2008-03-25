package com.cannontech.core.authentication.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.impl.PasswordHasher;

public abstract class MessageDigestHasher implements PasswordHasher {
    protected final MessageDigest messageDigest;
    
    public MessageDigestHasher(String algorithm) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(algorithm);
    }
    
    public final synchronized String hashPassword(final String password) {
        byte[] input = password.getBytes();
        byte[] raw = getMessageDigest().digest(input);
        String result = toHexString(raw);
        return result;
    }
    
    protected String toHexString(final byte[] input) {
        String hexString = CtiUtilities.toHexString(input);
        return hexString;
    }
    
    /**
     * Resetting the MessageDigest or applying any salt is done here.
     */
    protected abstract MessageDigest getMessageDigest();
}
