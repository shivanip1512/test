package com.cannontech.common.pao.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.model.GPS;
import com.cannontech.stars.util.StarsInvalidArgumentException;

public class LocationServiceImplTest {

    LocationServiceImpl impl = new LocationServiceImpl();

    @Test()
    public void testIsValidLatitudeandLongitude() {
        GPS gps = ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00), Double.toString(160.00));
        assertTrue("isValidLocationFormat valid, valid", gps != null);
        assertTrue("Latitude valid : valid", gps.getLatitude() == 85.00);
        assertTrue("Longitude valid : valid", gps.getLongitude() == 160.00);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitude_forString() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", "xyz", Double.toString(160.00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitude_forNaN() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(Double.NaN),
            Double.toString(160.00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitude_forNull() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", null, Double.toString(160.00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLongitude_forString() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00), "xyz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLongitude_forNaN() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00),
            Double.toString(Double.NaN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLongitude_forNull() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(85.00), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitudeandLongitude_forString() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", "xyz", "xyz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNaN() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(Double.NaN),
            Double.toString(Double.NaN));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNull() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNaNandNull() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat", Double.toString(Double.NaN), null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNullandNaN() {
        ReflectionTestUtils.invokeMethod(impl, "getValidLocationFormat",null, Double.toString(Double.NaN));
    }
}
