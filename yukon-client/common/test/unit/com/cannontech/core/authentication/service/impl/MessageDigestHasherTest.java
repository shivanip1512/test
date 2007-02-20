package com.cannontech.core.authentication.service.impl;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.core.authentication.service.impl.MessageDigestHasher;

/**
 * Note: If this test ever fails, there is a good chance that something has changed
 * that will prevent customers from logging into their system.
 * @author tmack
 *
 */
public class MessageDigestHasherTest {

    @Test
    public void testMD5Password() throws NoSuchAlgorithmException {
        MessageDigestHasher authenticationService = new MessageDigestHasher("MD5");
        String string = authenticationService.hashPassword("Tom Is Stupid!");
        Assert.assertEquals("Hash doesn't match precomputed value", "754b118eaa8457f636193782dcecb2e", string);
    }

    @Test
    public void testSHAPassword() throws NoSuchAlgorithmException {
        MessageDigestHasher authenticationService = new MessageDigestHasher("SHA");
        String string = authenticationService.hashPassword("Tom Is Stupid!");
        Assert.assertEquals("Hash doesn't match precomputed value", "3b385b6395434bb21f35c6a518228684aca41b", string);
        
    }
    
}
