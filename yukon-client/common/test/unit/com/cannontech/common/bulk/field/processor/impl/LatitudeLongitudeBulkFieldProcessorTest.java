package com.cannontech.common.bulk.field.processor.impl;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.stars.ws.LmDeviceDto;

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
    public void testIsLongitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.00, -1000.00);
    }

    @Test(expected = ProcessingException.class)
    public void testIsInvalidLongitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);

        ReflectionTestUtils.invokeMethod(deviceHelper, "locationValidation", paoIdentifier, 70.00, 1000.00);
    }

}
