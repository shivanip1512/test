package com.cannontech.core.authentication.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cannontech.common.util.CtiUtilities;

public class SimpleMessageDigestHasher {
    private final MessageDigest messageDigest;
    
    public SimpleMessageDigestHasher(final String algorithm) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(algorithm);
    }
    
    public final synchronized String hash(final String input) {
        return hash(input, null);
    }
    
    public final synchronized String hash(final String input, final byte[] salt) {
        messageDigest.reset();
        if (salt != null) {
            messageDigest.update(salt);
        }
        
        byte[] bytes = input.getBytes();
        byte[] raw = messageDigest.digest(bytes);
        String result = CtiUtilities.toHexString(raw);
        return result;
    }

}
