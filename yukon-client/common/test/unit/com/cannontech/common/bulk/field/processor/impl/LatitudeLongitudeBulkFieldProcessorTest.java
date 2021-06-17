package com.cannontech.common.bulk.field.processor.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

    @Test
    public void testIsInvalidLatitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        Assertions.assertThrows(ProcessingException.class, () -> {
            ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, -100.00, 100.00);
        });
    }

    @Test
    public void testIsLatitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        Assertions.assertThrows(ProcessingException.class, () -> {
            ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 100.00, 100.00);
        });
    }
    
    @Test
    public void testIsLatitudeNaN() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        Assertions.assertThrows(ProcessingException.class, () -> {
            ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, Double.valueOf(Double.NaN),
                    100.00);
        });
    }

    @Test
    public void testIsLongitudeNaN() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        Assertions.assertThrows(ProcessingException.class, () -> {
            ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.0,
                    Double.valueOf(Double.NaN));
        });
    }

    @Test
    public void testIsLongitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        Assertions.assertThrows(ProcessingException.class, () -> {
            ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.00, -1000.00);
        });
    }

    @Test
    public void testIsInvalidLongitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        Assertions.assertThrows(ProcessingException.class, () -> {
            ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.00, 1000.00);
        });
    }

    @Test()
    public void testIsRemoveLocation() {
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, null),
                "Remove null, null");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(70.00), null),
                "Remove valid, null");
        assertFalse(
                (boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, Double.valueOf(160.00)),
                "Remove null, valid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(45.00),
                Double.valueOf(160.00)), "Remove valid, valid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(545.00),
                Double.valueOf(160.00)), "Remove invalid, valid");
        assertFalse(
                (boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(545.00), null),
                "Remove invalid, null");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(45.00),
                Double.valueOf(190.00)), "Remove valid, invalid");
        assertFalse(
                (boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null, Double.valueOf(190.00)),
                "Remove null, invalid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN),
                Double.valueOf(80.00)), "Remove NaN, valid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN),
                Double.valueOf(190.00)), "Remove NaN, invalid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN),
                null), "Remove NaN, null");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(70.00),
                Double.valueOf(Double.NaN)), "Remove valid, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(200.00),
                Double.valueOf(Double.NaN)), "Remove invalid, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", null,
                Double.valueOf(Double.NaN)), "Remove null, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(Double.NaN),
                Double.valueOf(Double.NaN)), "Remove NaN, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isRemoveLocationData", Double.valueOf(545.00),
                Double.valueOf(190.00)), "Remove invalid, invalid");
    }
    
    @Test()
    public void testIsIgnoreLocation() {
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, null),
                "Ignore null, null");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(70.00), null),
                "Ignore valid, null");
        assertFalse(
                (boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, Double.valueOf(160.00)),
                "Ignore null, valid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(45.00),
                Double.valueOf(160.00)), "Ignore valid, valid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(545.00),
                Double.valueOf(160.00)), "Ignore invalid, valid");
        assertFalse(
                (boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(545.00), null),
                "Ignore invalid, null");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(45.00),
                Double.valueOf(190.00)), "Ignore valid, invalid");
        assertFalse(
                (boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null, Double.valueOf(190.00)),
                "Ignore null, invalid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN),
                Double.valueOf(80.00)), "Ignore NaN, valid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN),
                Double.valueOf(190.00)), "Ignore NaN, invalid");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN),
                null), "Ignore NaN, null");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(70.00),
                Double.valueOf(Double.NaN)), "Ignore valid, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(200.00),
                Double.valueOf(Double.NaN)), "Ignore invalid, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", null,
                Double.valueOf(Double.NaN)), "Ignore null, NaN");
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(Double.NaN),
                Double.valueOf(Double.NaN)), "Ignore NaN, NaN");
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(deviceHelper, "isIgnoreLocationData", Double.valueOf(545.00),
                Double.valueOf(190.00)), "Ignore invalid, invalid");
    }
}
