package com.cannontech.core.authentication.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cannontech.core.authentication.service.MessageDigestHasher;

public class SimpleMessageDigestHasher extends MessageDigestHasher {

    public SimpleMessageDigestHasher(final String algorithm) throws NoSuchAlgorithmException {
        super(algorithm);
    }
    
    @Override
    protected MessageDigest getMessageDigest() {
        messageDigest.reset();
        return messageDigest;
    }

}
