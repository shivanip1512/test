package com.cannontech.core.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.authentication.model.AuthenticationThrottleDto;

public class IncreasingAuthenticationThrottleImplTest {

    IncreasingAuthenticationThrottleServiceImpl authenticationThrottleServiceImpl;
    String testUser1 = "TestUser1";
    String testUser2 = "TestUser2";

    @BeforeEach
    public void setUp() throws Exception {
        authenticationThrottleServiceImpl = new IncreasingAuthenticationThrottleServiceImpl();
        authenticationThrottleServiceImpl.setAuthThrottleExpBase(Math.E/2);
        authenticationThrottleServiceImpl.setAuthThrottleDelta(0.0);
    }

    @Test
    public void testLoginAttemptedTooSoon() throws Exception {
        Assertions.assertThrows(AuthenticationThrottleException.class, () -> {
            for (int i = 0; i < 10; i++) {
                authenticationThrottleServiceImpl.loginAttempted(testUser1);
                AuthenticationThrottleDto throttleData = authenticationThrottleServiceImpl
                        .getAuthenticationThrottleData(testUser1);
                assertTrue(throttleData.getActualThrottleDuration() > 0, "Throttle duration incorrect");
            }
        });
    }
    
    @Test
    public void testLoginAttempted() throws Exception {
        
        authenticationThrottleServiceImpl.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData1.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        for (int i = 0; i < 10; i++) {
            try {
                authenticationThrottleServiceImpl.loginAttempted(testUser1);
            } catch (AuthenticationThrottleException e) { /* do nothing, continue */ }
        }
        
        AuthenticationThrottleDto throttleData2 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData2.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        //later login attempts have increasing wait times
        assertTrue(throttleData2.compareTo(throttleData1) >= 0, "Throttle duration incorrect");
    }    

    @Test
    public void testLoginSucceeded() throws Exception {
        authenticationThrottleServiceImpl.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);        
        assertTrue(throttleData1.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        authenticationThrottleServiceImpl.loginSucceeded(testUser1);
        AuthenticationThrottleDto throttleData2 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData2 == null, "Throttle duration incorrect");
    }

    @Test
    public void testGetAuthenticationThrottleData() throws Exception {
        authenticationThrottleServiceImpl.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData1.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        long throttleSeconds2 = 0;
        for (int i = 0; i < 10; i++) {
            try {
                authenticationThrottleServiceImpl.loginAttempted(testUser1);
            } catch (AuthenticationThrottleException e){
                //new wait time is on the exception, in case of early login attempts
                throttleSeconds2 = e.getThrottleSeconds();
            }
        }
        AuthenticationThrottleDto throttleData2 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData2.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        //later login attempts have increasing wait times
        assertTrue(throttleData2.compareTo(throttleData1) >= 0, "Throttle duration incorrect");

        //as time elapses, wait seconds reduce
        AuthenticationThrottleDto throttleData3 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData3 != null, "Throttle duration incorrect");

        long throttleSeconds3 = throttleData3.getThrottleDurationSeconds();
        assertTrue(throttleSeconds3 <= throttleSeconds2, "Throttle duration incorrect");
    }

    @Test
    public void testRemoveAuthenticationThrottle() throws Exception {
        authenticationThrottleServiceImpl.loginAttempted(testUser1);
        AuthenticationThrottleDto throttleData1 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(throttleData1.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        authenticationThrottleServiceImpl.removeAuthenticationThrottle(testUser1);
        AuthenticationThrottleDto authThrottleDto = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(authThrottleDto == null, "Throttle duration incorrect");
    }

    @Test
    public void testCleanupAuthenticationThrottle() throws Exception {
        for (int i = 0; i < 10; i++) {
            try {
              authenticationThrottleServiceImpl.loginAttempted(testUser1);
            } catch (AuthenticationThrottleException e) {  /* do nothing, continue */ }
            
            AuthenticationThrottleDto throttleData1 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
            assertTrue(throttleData1.getActualThrottleDuration() > 0, "Throttle duration incorrect");
        }
        
        for (int i = 0; i < 10; i++) {
            try {
              authenticationThrottleServiceImpl.loginAttempted(testUser2);
            } catch (AuthenticationThrottleException e) { /* do nothing, continue */ }

            AuthenticationThrottleDto throttleData2 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser2);
            assertTrue(throttleData2.getActualThrottleDuration() > 0, "Throttle duration incorrect");
        }

        AuthenticationThrottleDto authThrottleDto1 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(authThrottleDto1 != null, "Throttle duration incorrect");
        assertTrue(authThrottleDto1.getActualThrottleDuration() > 0, "Throttle duration incorrect");

        AuthenticationThrottleDto authThrottleDto2 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser2);
        assertTrue(authThrottleDto2 != null, "Throttle duration incorrect");
        assertTrue(authThrottleDto2.getActualThrottleDuration() > 0, "Throttle duration incorrect");
        
        // artificial abadonedAuthThrottleDays to test cleanup
        authenticationThrottleServiceImpl.setAbandonedAuthThrottleDays(-1);  // We're setting this to a day in the future to wipe out everything.
        authenticationThrottleServiceImpl.cleanupAuthenticationThrottle();

        AuthenticationThrottleDto authThrottleDto3 =authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser1);
        assertTrue(authThrottleDto3 == null, "Throttle duration incorrect");

        AuthenticationThrottleDto authThrottleDto4 = authenticationThrottleServiceImpl.getAuthenticationThrottleData(testUser2);
        assertTrue(authThrottleDto4 == null, "Throttle duration incorrect");

    }
}