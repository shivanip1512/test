package com.cannontech.stars.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.model.GPS;
import com.cannontech.stars.util.StarsInvalidArgumentException;

public class LmDeviceDtoConverterImplTest {

    LmDeviceDtoConverterImpl impl = new LmDeviceDtoConverterImpl();

    @Test
    public void testIsValidGps() throws Exception {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = null;
        GPS gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps == null, "buildGps null, null");

        locationData[27] = Double.toString(45.00);
        locationData[28] = Double.toString(160.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps != null, "buildGps valid, valid");
        assertTrue(gps.getLatitude() == 45.00, "Latitude valid : valid");
        assertTrue(gps.getLongitude() == 160.00, "Longitude valid : valid");

        locationData[27] = Double.toString(245.00);
        locationData[28] = Double.toString(260.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps != null, "buildGps invalid, invalid");
        assertTrue(gps.getLatitude() == 245.00, "Latitude invalid : invalid");
        assertTrue(gps.getLongitude() == 260.00, "Longitude invalid : invalid");

        locationData[27] = "NULL";
        locationData[28] = "NULL";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps != null, "buildGps NULL, NULL");
        assertTrue(gps.getLatitude() == null, "Latitude NULL : null");
        assertTrue(gps.getLongitude() == null, "Longitude NULL : null");

        locationData[27] = "DELETE";
        locationData[28] = "DELETE";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps != null, "buildGps DELETE, DELETE");
        assertTrue(gps.getLatitude() == null, "Latitude DELETE : null");
        assertTrue(gps.getLongitude() == null, "Longitude DELETE : null");

        locationData[27] = "NULL";
        locationData[28] = "DELETE";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps != null, "buildGps NULL, DELETE");
        assertTrue(gps.getLatitude() == null, "Latitude NULL : null");
        assertTrue(gps.getLongitude() == null, "Longitude DELETE : null");

        locationData[27] = "DELETE";
        locationData[28] = "NULL";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue(gps != null, "buildGps DELETE, NULL");
        assertTrue(gps.getLatitude() == null, "Latitude DELETE : null");
        assertTrue(gps.getLongitude() == null, "Longitude NULL : null");
    }

    @Test
    public void test_invalidLatitude_forString() {
        String[] locationData = new String[29];

        locationData[27] = "xyz";
        locationData[28] = Double.toString(160.00);
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLatitude_forNaN() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = Double.toString(160.00);
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLatitude_forNull() {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = Double.toString(160.00);
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLongitude_forString() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(45.00);
        locationData[28] = "xyz";
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLongitude_forNaN() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(45.00);
        locationData[28] = Double.toString(Double.NaN);
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLongitude_forNull() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(45.00);
        locationData[28] = null;
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLatitudeandLongitude_forString() {
        String[] locationData = new String[29];

        locationData[27] = "xyz";
        locationData[28] = "xyz";
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLatitudeandLongitude_forNaN() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = Double.toString(Double.NaN);
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLatitudeandLongitude_forNaNandNull() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = null;
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

    @Test
    public void test_invalidLatitudeandLongitude_forNullandNaN() {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = Double.toString(Double.NaN);
        Assertions.assertThrows(StarsInvalidArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        });
    }

}
