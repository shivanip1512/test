package com.cannontech.core.authentication.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class YukonPasswordHasher implements PasswordHasher {
    private final MessageDigest messageDigest;
    // the following value must never, ever, ever be changed
    private static final String salt = "88302c9d15581fd7abc6aa6742cf71b9da852d33";

    public YukonPasswordHasher(final String algorithm) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(algorithm);
    }

    @Override
    public String hashPassword(String password) {
        messageDigest.reset();
        messageDigest.update(salt.getBytes());

        byte[] input = password.getBytes();
        byte[] raw = messageDigest.digest(input);
        String result = toHexString(raw);
        return result;
    }

    private String toHexString(final byte[] input) {
        Formatter hexFormatter = new Formatter();
        for (byte b : input) {
            hexFormatter.format("%x", b);
        }
        return hexFormatter.out().toString();
    }

}
