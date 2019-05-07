package com.cannontech.common.bulk.field.processor.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public class LatitudeLongitudeBulkFieldProcessorTest {

    LatitudeLongitudeBulkFieldProcessor deviceHelper = new LatitudeLongitudeBulkFieldProcessor();

    @Test
    public void testIsValidLocation() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 45.00, 160.00);
    }

    @Test(expected = ProcessingException.class)
    public void testIsInvalidLatitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, -100.00, 100.00);
    }

    @Test(expected = ProcessingException.class)
    public void testIsLatitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 100.00, 100.00);
    }
    
    @Test(expected = ProcessingException.class)
    public void testIsLatitudeNaN() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, Double.valueOf(Double.NaN),
            100.00);
    }

    @Test(expected = ProcessingException.class)
    public void testIsLongitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.00, -1000.00);
    }

    @Test(expected = ProcessingException.class)
    public void testIsInvalidLongitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.00, 1000.00);
    }

    @Test()
    public void testIsRemoveLocation() {
        assertTrue("Remove null, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, null));
        assertFalse("Remove valid, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(70.0), null));
        assertFalse("Remove null, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, Double.valueOf(160.00)));
        assertFalse("Remove valid, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(45.00), Double.valueOf(160.00)));
    }
    
    @Test()
    public void testIsIgnoreLocation() {
        assertTrue("Ignore NaN, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN), Double.valueOf(Double.NaN)));
        assertFalse("Ignore null, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, null));
        assertFalse("Ignore NaN, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN), null));
        assertFalse("Ignore null, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, Double.valueOf(Double.NaN)));
        assertFalse("Ignore valid, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(45.00), Double.valueOf(Double.NaN)));
        assertFalse("Ignore valid, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(45.00), Double.valueOf(160.00)));
    }    
}
