package com.cannontech.core.authentication.service.impl;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Note: If this test ever fails, there is a good chance that something has changed
 * that will prevent customers from logging into their system.
 * @author tmack
 *
 */
public class YukonPasswordHasherTest {
    
    @Test
    public void test_YukonPasswordHasher_MD5Password() throws NoSuchAlgorithmException {
        PasswordHasher authenticationService = new YukonPasswordHasher("MD5");
        String string = authenticationService.hashPassword("Tom Is Stupid!");
        Assert.assertEquals("Hash doesn't match precomputed value", "754b118eaa8457f636193782dcecb2e", string);
    }

    @Test
    public void test_YukonPasswordHasher_SHAPassword() throws NoSuchAlgorithmException {
        PasswordHasher authenticationService = new YukonPasswordHasher("SHA");
        String string = authenticationService.hashPassword("Tom Is Stupid!");
        Assert.assertEquals("Hash doesn't match precomputed value", "3b385b6395434bb21f35c6a518228684aca41b", string);
        
    }
    
//    @Test
//    public void test_SimpleMessageDigestHasher_MD5Password() throws NoSuchAlgorithmException {
//        MessageDigestHasher authenticationService = new SimpleMessageDigestHasher("MD5");
//        String string = authenticationService.hashPassword("Tom Is Stupid!");
//        Assert.assertEquals("Hash doesn't match precomputed value", "ff7203c1c33518eeac475b9fa94646a0", string);
//    }
//
//    @Test
//    public void test_SimpleMessageDigestHasher_SHAPassword() throws NoSuchAlgorithmException {
//        MessageDigestHasher authenticationService = new SimpleMessageDigestHasher("SHA");
//        String string = authenticationService.hashPassword("Tom Is Stupid!");
//        Assert.assertEquals("Hash doesn't match precomputed value", "88302c9d15581fd7abc6aa6742cf71b9da852d33", string);
//        
//    }
    
}
