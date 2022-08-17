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
    public void testIsLongitudeNaN() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.0,
            Double.valueOf(Double.NaN));
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
        assertFalse("Remove valid, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(70.00), null));
        assertFalse("Remove null, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, Double.valueOf(160.00)));
        assertFalse("Remove valid, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(45.00), Double.valueOf(160.00)));
        assertFalse("Remove invalid, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(545.00), Double.valueOf(160.00)));
        assertFalse("Remove invalid, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(545.00),null));
        assertFalse("Remove valid, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(45.00), Double.valueOf(190.00)));
        assertFalse("Remove null, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, Double.valueOf(190.00)));
        assertFalse("Remove NaN, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN), Double.valueOf(80.00)));
        assertFalse("Remove NaN, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN), Double.valueOf(190.00)));
        assertFalse("Remove NaN, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN), null));
        assertFalse("Remove valid, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(70.00), Double.valueOf(Double.NaN)));
        assertFalse("Remove invalid, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(200.00), Double.valueOf(Double.NaN)));
        assertFalse("Remove null, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null , Double.valueOf(Double.NaN)));
        assertFalse("Remove NaN, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN), Double.valueOf(Double.NaN)));
        assertFalse("Remove invalid, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(545.00), Double.valueOf(190.00)));
    }
    
    @Test()
    public void testIsIgnoreLocation() {
        assertFalse("Ignore null, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, null));
        assertFalse("Ignore valid, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(70.00), null));
        assertFalse("Ignore null, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, Double.valueOf(160.00)));
        assertFalse("Ignore valid, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(45.00), Double.valueOf(160.00)));
        assertFalse("Ignore invalid, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(545.00), Double.valueOf(160.00)));
        assertFalse("Ignore invalid, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(545.00),null));
        assertFalse("Ignore valid, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(45.00), Double.valueOf(190.00)));
        assertFalse("Ignore null, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, Double.valueOf(190.00)));
        assertFalse("Ignore NaN, valid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN), Double.valueOf(80.00)));
        assertFalse("Ignore NaN, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN), Double.valueOf(190.00)));
        assertFalse("Ignore NaN, null", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN), null));
        assertFalse("Ignore valid, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(70.00), Double.valueOf(Double.NaN)));
        assertFalse("Ignore invalid, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(200.00), Double.valueOf(Double.NaN)));
        assertFalse("Ignore null, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null , Double.valueOf(Double.NaN)));
        assertTrue("Ignore NaN, NaN", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN), Double.valueOf(Double.NaN)));
        assertFalse("Ignore invalid, invalid", ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(545.00), Double.valueOf(190.00)));
    }
}
