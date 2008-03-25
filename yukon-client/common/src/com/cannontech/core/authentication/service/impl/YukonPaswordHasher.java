package com.cannontech.core.authentication.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import com.cannontech.core.authentication.service.MessageDigestHasher;

public class YukonPaswordHasher extends MessageDigestHasher {
    // the following value must never, ever, ever be changed
    private static final String salt = "88302c9d15581fd7abc6aa6742cf71b9da852d33";

    public YukonPaswordHasher(final String algorithm) throws NoSuchAlgorithmException {
        super(algorithm);
    }

    /**
     * Overriding MessageDigestHasher.toHexString in this case is required!  Do not change this...
     * The original formatter didn't take into account for leading 0's being stripped off.
     * Changing this will break all existing hashed passwords.
     */
    @Override
    protected String toHexString(final byte[] input) {
        Formatter hexFormatter = new Formatter();
        for (byte b : input) {
            hexFormatter.format("%x", b);
        }
        return hexFormatter.out().toString();
    }
    
    @Override
    protected MessageDigest getMessageDigest() {
        messageDigest.reset();
        messageDigest.update(salt.getBytes());
        return messageDigest;
    }

}
