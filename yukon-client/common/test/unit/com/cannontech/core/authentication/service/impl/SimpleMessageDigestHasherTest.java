package com.cannontech.core.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class SimpleMessageDigestHasherTest {

    @Test
    public void test_MD5Password() throws NoSuchAlgorithmException {
        SimpleMessageDigestHasher simpleHasher = new SimpleMessageDigestHasher("MD5");
        String string = simpleHasher.hash("Tom Is Stupid!");
        assertEquals("ff7203c1c33518eeac475b9fa94646a0", string, "Hash doesn't match precomputed value");
    }

    @Test
    public void test_SHAPassword() throws NoSuchAlgorithmException {
        SimpleMessageDigestHasher simpleHasher = new SimpleMessageDigestHasher("SHA");
        String string = simpleHasher.hash("Tom Is Stupid!");
        assertEquals("88302c9d15581fd7abc6aa6742cf71b9da852d33", string, "Hash doesn't match precomputed value");
    }

}
