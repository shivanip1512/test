package com.cannontech.common.pao.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.model.GPS;

public class LocationServiceImplTest {

    LocationServiceImpl impl = new LocationServiceImpl();

    @Test
    public void testIsValidLatitudeandLongitude() {
        GPS gps = ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00), Double.toString(160.00));
        assertTrue( gps != null, "isValidLocationFormat valid, valid");
        assertTrue(gps.getLatitude() == 85.00, "Latitude valid : valid");
        assertTrue(gps.getLongitude() == 160.00, "Longitude valid : valid");
    }

    @Test
    public void test_invalidLatitude_forString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", "xyz", Double.toString(160.00));
        });
    }

    @Test
    public void test_invalidLatitude_forNaN() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(Double.NaN),
                    Double.toString(160.00));
        });
    }

    @Test
    public void test_invalidLatitude_forNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", null, Double.toString(160.00));
        });
    }

    @Test
    public void test_invalidLongitude_forString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00), "xyz");
        });
    }

    @Test
    public void test_invalidLongitude_forNaN() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00),
                    Double.toString(Double.NaN));
        });
    }

    @Test
    public void test_invalidLongitude_forNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00), null);
        });
    }

    @Test
    public void test_invalidLatitudeandLongitude_forString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", "xyz", "xyz");
        });
    }

    @Test
    public void test_invalidLatitudeandLongitude_forNaN() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(Double.NaN),
                    Double.toString(Double.NaN));
        });
    }
    
    @Test
    public void test_invalidLatitudeandLongitude_forNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", null, null);
        });
    }
    
    @Test
    public void test_invalidLatitudeandLongitude_forNaNandNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(Double.NaN), null);
        });
    }
    
    @Test
    public void test_invalidLatitudeandLongitude_forNullandNaN() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat",null, Double.toString(Double.NaN));
        });
    }
}
