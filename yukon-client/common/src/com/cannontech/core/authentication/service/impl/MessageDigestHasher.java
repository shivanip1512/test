package com.cannontech.core.authentication.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class MessageDigestHasher implements PasswordHasher {
    private MessageDigest messageDigest;
    // the following value must never, ever, ever be changed
    private String salt = "88302c9d15581fd7abc6aa6742cf71b9da852d33";

    public MessageDigestHasher(String digestAlgorithm) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(digestAlgorithm);
    }
    
    public String hashPassword(String password) {
        String result;
        synchronized (messageDigest) {
            messageDigest.reset();
            messageDigest.update(salt.getBytes());
            byte[] bs = messageDigest.digest(password.getBytes());
            result = binaryToString(bs);
        }
        
        return result;
    }

    protected String binaryToString(byte[] bs) {
        Formatter hexFormatter = new Formatter();
        for (byte b : bs) {
            hexFormatter.format("%x", b);
        }
        return hexFormatter.out().toString();
    }

    public void setMessageDigest(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }


}
