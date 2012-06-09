package com.cannontech.core.authentication.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;

public class AuthenticationThrottleHelperImplTest {

    AuthenticationThrottleHelperImpl helper;
    String testUser1 = "TestUser1";
    String testUser2 = "TestUser2";

    @Before
    public void setUp() throws Exception {
        helper = new AuthenticationThrottleHelperImpl();
        helper.setAuthThrottleExpBase(Math.E/2);
        helper.setAuthThrottleDelta(0.0);
        helper.resetAll();
    }

    @Test (expected=AuthenticationThrottleException.class)
    public void testLoginAttemptedTooSoon() throws Exception {
        for (int i = 0; i < 10; i++) {
            helper.loginAttempted(testUser1);
            AuthenticationThrottleDto throttleData = helper.getAuthenticationThrottleData(testUser1);
            assertTrue("Throttle duration incorrect", throttleData.getActualThrottleDuration() > 0);
        }
    }
    
    @Test
    public void testLoginAttempted() throws Exception {
        
        helper.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData1.getActualThrottleDuration() > 0);

        for (int i = 0; i < 10; i++) {
            try {
                helper.loginAttempted(testUser1);
            } catch (AuthenticationThrottleException e){
                //do nothing, continue
            }
        }
        
        AuthenticationThrottleDto throttleData2 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData2.getActualThrottleDuration() > 0);

        //later login attempts have increasing wait times
        assertTrue("Throttle duration incorrect", throttleData2.compareTo(throttleData1) >= 0);
    }    

    @Test
    public void testLoginSucceeded() throws Exception {
        helper.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = helper.getAuthenticationThrottleData(testUser1);        
        assertTrue("Throttle duration incorrect", throttleData1.getActualThrottleDuration() > 0);

        helper.loginSucceeded(testUser1);
        AuthenticationThrottleDto throttleData2 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData2 == null);
    }

    @Test
    public void testGetAuthenticationThrottleData() throws Exception {
        helper.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData1.getActualThrottleDuration() > 0);

        long throttleSeconds2 = 0;
        for (int i = 0; i < 10; i++) {
            try {
                helper.loginAttempted(testUser1);
            } catch (AuthenticationThrottleException e){
                //new wait time is on the exception, in case of early login attempts
                throttleSeconds2 = e.getThrottleSeconds();
            }
        }
        AuthenticationThrottleDto throttleData2 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData2.getActualThrottleDuration() > 0);

        //later login attempts have increasing wait times
        assertTrue("Throttle duration incorrect", throttleData2.compareTo(throttleData1) >= 0);

        //as time elapses, wait seconds reduce
        AuthenticationThrottleDto throttleData3 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData3 != null);

        long throttleSeconds3 = throttleData3.getThrottleDurationSeconds();
        assertTrue("Throttle duration incorrect", throttleSeconds3 <= throttleSeconds2);
    }

    @Test
    public void testRemoveAuthenticationThrottle() throws Exception {
        helper.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", throttleData1.getActualThrottleDuration() > 0);

        helper.removeAuthenticationThrottle(testUser1);
        AuthenticationThrottleDto authThrottleDto = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", authThrottleDto == null);
    }

    @Test
    public void testCleanupAuthenticationThrottle() throws Exception {
        for (int i = 0; i < 10; i++) {
            try {
              helper.loginAttempted(testUser1);
            } catch (AuthenticationThrottleException e) {
                // do nothing, continue
            }
            AuthenticationThrottleDto throttleData1 = helper.getAuthenticationThrottleData(testUser1);
            assertTrue("Throttle duration incorrect",
                       throttleData1.getActualThrottleDuration() > 0);
        }
        
        for (int i = 0; i < 10; i++) {
            try {
              helper.loginAttempted(testUser2);
            } catch (AuthenticationThrottleException e) {
                // do nothing, continue
            }
            AuthenticationThrottleDto throttleData2 = helper.getAuthenticationThrottleData(testUser2);
            assertTrue("Throttle duration incorrect",
                       throttleData2.getActualThrottleDuration() > 0);
        }

        AuthenticationThrottleDto authThrottleDto1 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", authThrottleDto1 != null);
        assertTrue("Throttle duration incorrect", authThrottleDto1.getActualThrottleDuration() > 0);

        AuthenticationThrottleDto authThrottleDto2 = helper.getAuthenticationThrottleData(testUser2);
        assertTrue("Throttle duration incorrect", authThrottleDto2 != null);
        assertTrue("Throttle duration incorrect", authThrottleDto2.getActualThrottleDuration() > 0);

        // artificial abadonedAuthThrottleDays to test cleanup
        helper.setAbandonedAuthThrottleDays(1);
        helper.cleanupAuthenticationThrottle();

        AuthenticationThrottleDto authThrottleDto3 = helper.getAuthenticationThrottleData(testUser1);
        assertTrue("Throttle duration incorrect", authThrottleDto3 == null);

        AuthenticationThrottleDto authThrottleDto4 = helper.getAuthenticationThrottleData(testUser2);
        assertTrue("Throttle duration incorrect", authThrottleDto4 == null);
    }
}