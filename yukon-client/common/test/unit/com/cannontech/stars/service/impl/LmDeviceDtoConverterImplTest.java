package com.cannontech.stars.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.model.GPS;

public class LmDeviceDtoConverterImplTest {

    LmDeviceDtoConverterImpl impl = new LmDeviceDtoConverterImpl();

    @Test()
    public void testIsValidGps() throws Exception {
        String[] locationData = new String[29];

        locationData[27] = null;
        locationData[28] = null;
        GPS gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps null, null", gps == null);

        locationData[27] = null;
        locationData[28] = Double.toString(160.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps null, valid", gps != null);
        assertTrue("Latitude null : null", gps.getLatitude() == null);
        assertTrue("Longitude valid : valid", gps.getLongitude() == 160.00);

        locationData[27] = Double.toString(70.00);
        locationData[28] = null;
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps valid, null", gps != null);
        assertTrue("Latitude valid : valid", gps.getLatitude() == 70.00);
        assertTrue("Longitude null : null", gps.getLongitude() == null);

        locationData[27] = Double.toString(45.00);
        locationData[28] = Double.toString(160.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps valid, valid", gps != null);
        assertTrue("Latitude valid : valid", gps.getLatitude() == 45.00);
        assertTrue("Longitude valid : valid", gps.getLongitude() == 160.00);

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = Double.toString(160.00);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps NaN, valid", gps != null);
        assertTrue("Latitude NaN : NaN", gps.getLatitude().isNaN());
        assertTrue("Longitude valid : valid", gps.getLongitude() == 160.00);

        locationData[27] = Double.toString(45.00);
        locationData[28] = Double.toString(Double.NaN);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps valid, NaN", gps != null);
        assertTrue("Latitude valid : valid", gps.getLatitude() == 45.00);
        assertTrue("Longitude NaN : NaN", gps.getLongitude().isNaN());

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = Double.toString(Double.NaN);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps NaN, NaN", gps != null);
        assertTrue("Latitude NaN : NaN", gps.getLatitude().isNaN());
        assertTrue("Longitude NaN : NaN", gps.getLongitude().isNaN());

        locationData[27] = Double.toString(Double.NaN);
        locationData[28] = null;
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps NaN, null", gps != null);
        assertTrue("Latitude NaN : NaN", gps.getLatitude().isNaN());
        assertTrue("Longitude null : null", gps.getLongitude() == null);

        locationData[27] = null;
        locationData[28] = Double.toString(Double.NaN);
        gps = ReflectionTestUtils.invokeMethod(impl, "getLocationGps", new Object[] { locationData });
        assertTrue("buildGps null, NaN", gps != null);
        assertTrue("Latitude null : null", gps.getLatitude() == null);
        assertTrue("Longitude NaN : NaN", gps.getLongitude().isNaN());

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

}
