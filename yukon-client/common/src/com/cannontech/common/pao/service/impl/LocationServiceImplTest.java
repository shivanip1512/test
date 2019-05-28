package com.cannontech.common.pao.service.impl;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.model.GPS;
import com.cannontech.stars.util.StarsInvalidArgumentException;

public class LocationServiceImplTest {

    LocationServiceImpl impl = new LocationServiceImpl();

    @Test(expected = StarsInvalidArgumentException.class)
    public void testIsValidLatitudeandLongitude() {
        String latitude = StringUtils.EMPTY;
        String longitude = StringUtils.EMPTY;

        latitude = Double.toString(85.00);
        longitude = Double.toString(160.00);
        GPS gps = ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);
        assertTrue("isValidLocationFormat valid, valid", gps != null);
        assertTrue("Latitude valid : valid", gps.getLatitude() == 85.00);
        assertTrue("Longitude valid : valid", gps.getLongitude() == 160.00);

        latitude = "xyz";
        longitude = "xyz";
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = "xyz";
        longitude = Double.toString(160.00);
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = Double.toString(85.00);
        longitude = "xyz";
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = Double.toString(Double.NaN);
        longitude = Double.toString(160.00);
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = Double.toString(85.00);
        longitude = Double.toString(Double.NaN);
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = Double.toString(Double.NaN);
        longitude = Double.toString(Double.NaN);
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = Double.toString(Double.NaN);
        longitude = null;
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = null;
        longitude = Double.toString(Double.NaN);
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

        latitude = null;
        longitude = null;
        ReflectionTestUtils.invokeMethod(impl, "isValidLocationFormat", latitude, longitude);

    }
}
