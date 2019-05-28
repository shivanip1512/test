package com.cannontech.stars.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.model.GPS;
import com.cannontech.stars.util.StarsInvalidArgumentException;

public class LmDeviceDtoConverterImplTest {

    LmDeviceDtoConverterImpl impl = new LmDeviceDtoConverterImpl();

    @Test()
    public void testIsValidGps() throws Exception {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = null;
        GPS gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps null, null", gps == null);

        locationData[27] = Double.toString(45.00);
        locationData[28] = Double.toString(160.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps valid, valid", gps != null);
        assertTrue("Latitude valid : valid", gps.getLatitude() == 45.00);
        assertTrue("Longitude valid : valid", gps.getLongitude() == 160.00);

        locationData[27] = Double.toString(245.00);
        locationData[28] = Double.toString(260.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps invalid, invalid", gps != null);
        assertTrue("Latitude invalid : invalid", gps.getLatitude() == 245.00);
        assertTrue("Longitude invalid : invalid", gps.getLongitude() == 260.00);

        locationData[27] = "NULL";
        locationData[28] = "NULL";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps NULL, NULL", gps != null);
        assertTrue("Latitude NULL : null", gps.getLatitude() == null);
        assertTrue("Longitude NULL : null", gps.getLongitude() == null);

        locationData[27] = "DELETE";
        locationData[28] = "DELETE";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps DELETE, DELETE", gps != null);
        assertTrue("Latitude DELETE : null", gps.getLatitude() == null);
        assertTrue("Longitude DELETE : null", gps.getLongitude() == null);

        locationData[27] = "NULL";
        locationData[28] = "DELETE";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps NULL, DELETE", gps != null);
        assertTrue("Latitude NULL : null", gps.getLatitude() == null);
        assertTrue("Longitude DELETE : null", gps.getLongitude() == null);

        locationData[27] = "DELETE";
        locationData[28] = "NULL";
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps DELETE, NULL", gps != null);
        assertTrue("Latitude DELETE : null", gps.getLatitude() == null);
        assertTrue("Longitude NULL : null", gps.getLongitude() == null);
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitude_forString() {
        String[] locationData = new String[29];

        locationData[27] = "xyz";
        locationData[28] = Double.toString(160.00);
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitude_forNaN() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = Double.toString(160.00);
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitude_forNull() {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = Double.toString(160.00);
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLongitude_forString() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(45.00);
        locationData[28] = "xyz";
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLongitude_forNaN() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(45.00);
        locationData[28] = Double.toString(Double.NaN);
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLongitude_forNull() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(45.00);
        locationData[28] = null;
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitudeandLongitude_forString() {
        String[] locationData = new String[29];

        locationData[27] = "xyz";
        locationData[28] = "xyz";
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNaN() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = Double.toString(Double.NaN);
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNaNandNull() {
        String[] locationData = new String[29];

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = null;
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

    @Test(expected = StarsInvalidArgumentException.class)
    public void test_invalidLatitudeandLongitude_forNullandNaN() {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = Double.toString(Double.NaN);
        ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
    }

}
