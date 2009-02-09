package com.cannontech.core.authentication.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationTimeoutHelperImplTest {

    AuthenticationTimeoutHelperImpl helper;
    String testUser1 = "TestUser1";
    String testUser2 = "TestUser2";

//    @Before
//    public void setUp() throws Exception {
//        helper = new AuthenticationTimeoutHelperImpl();
//        helper.reset();
//    }
//
//    @Test
//    public void testAddAuthenticationTimeout() {
//        long timeout1 = helper.addAuthenticationTimeout(testUser1);
//        assertTrue("Timeout duration incorrect", timeout1 > 0);
//
//        long timeout2 = helper.addAuthenticationTimeout(testUser1);
//        assertTrue("Timeout duration incorrect", timeout2 > 0);
//
//        assertTrue("Timeout duration incorrect", timeout2 > timeout1);
//    }
//
//    @Test
//    public void testRemoveAuthenticationTimeout() {
//        long timeout1 = helper.addAuthenticationTimeout(testUser1);
//        assertTrue("Timeout duration incorrect", timeout1 > 0);
//
//        helper.removeAuthenticationTimeout(testUser1);
//        long timeout2 = helper.getAuthenticationTimeoutDuration(testUser1);
//        assertTrue("Timeout duration incorrect", timeout2 == 0);
//    }
//
//    @Test
//    public void testGetAuthenticationTimeout() {
//        long timeout1 = helper.addAuthenticationTimeout(testUser1);
//        assertTrue("Timeout duration incorrect", timeout1 > 0);
//
//        long timeout2 = helper.addAuthenticationTimeout(testUser1);
//        assertTrue("Timeout duration incorrect", timeout2 > 0);
//
//        assertTrue("Timeout duration incorrect", timeout2 > timeout1);
//
//        long timeout3 = helper.getAuthenticationTimeoutDuration(testUser1);
//        assertTrue("Timeout duration incorrect", timeout3 <= timeout2);
//    }
//
//    @Test
//    public void testCleanupAuthenticationTimeout() {
//        long timeout1 = 0;
//        long timeout2 = 0;
//        for (int i = 0; i < 10; i++) {
//            timeout1 = helper.addAuthenticationTimeout(testUser1);
//            assertTrue("Timeout duration incorrect", timeout1 > 0);
//
//            timeout2 = helper.addAuthenticationTimeout(testUser2);
//            assertTrue("Timeout duration incorrect", timeout2 > 0);
//        }
//
//        timeout1 = helper.getAuthenticationTimeoutDuration(testUser1);
//        assertTrue("Timeout duration incorrect", timeout1 > 0);
//
//        timeout2 = helper.getAuthenticationTimeoutDuration(testUser2);
//        assertTrue("Timeout duration incorrect", timeout2 > 0);
//
//        // artificial abadonedAuthTimeoutDays to test cleanup
//        helper.setAbandonedAuthTimeoutDays(1);
//        helper.cleanupAuthenticationTimeout();
//
//        long timeout3 = helper.getAuthenticationTimeoutDuration(testUser1);
//        assertTrue("Timeout duration incorrect", timeout3 == 0);
//
//        long timeout4 = helper.getAuthenticationTimeoutDuration(testUser2);
//        assertTrue("Timeout duration incorrect", timeout4 == 0);
//    }
}
