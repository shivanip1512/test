package com.cannontech.stars.ws;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;


public class StarsControllableDeviceHelperTest {
    StarsControllableDeviceHelperImpl deviceHelper = new StarsControllableDeviceHelperImpl();

    @Test
    public void testIsOperationAllowedForDeviceNest() {
        boolean isOperationAllowed =
            ReflectionTestUtils.invokeMethod(deviceHelper, "isOperationAllowedForHardware", HardwareType.NEST_THERMOSTAT);
        assertFalse("Is operation allowed", isOperationAllowed);
    }

    @Test
    public void testIsOperationAllowedForDeviceNonNest() {
        boolean isOperationAllowed =
            ReflectionTestUtils.invokeMethod(deviceHelper, "isOperationAllowedForHardware", HardwareType.HONEYWELL_9000);
        assertTrue("Is operation allowed", isOperationAllowed);
    }

    @Test
    public void testIsvalidLocation() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);
        DisplayablePao displayablePao = new DisplayableDevice(paoIdentifier, "2879");
        LmDeviceDto lmDeviceDto = new LmDeviceDto();
        lmDeviceDto.setLatitude(45.00);
        lmDeviceDto.setLongitude(160.00);

        ReflectionTestUtils.invokeMethod(deviceHelper, "validateForLocation", displayablePao, lmDeviceDto);
    }

    @Test(expected = ProcessingException.class)
    public void testIsInvalidLatitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);
        DisplayablePao displayablePao = new DisplayableDevice(paoIdentifier, "2879");
        LmDeviceDto lmDeviceDto = new LmDeviceDto();
        lmDeviceDto.setLatitude(-100.00);
        lmDeviceDto.setLongitude(100.00);

        ReflectionTestUtils.invokeMethod(deviceHelper, "validateForLocation", displayablePao, lmDeviceDto);
    }

    @Test(expected = ProcessingException.class)
    public void testIsLatitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);
        DisplayablePao displayablePao = new DisplayableDevice(paoIdentifier, "2879");
        LmDeviceDto lmDeviceDto = new LmDeviceDto();
        lmDeviceDto.setLatitude(100.00);
        lmDeviceDto.setLongitude(100.00);

        ReflectionTestUtils.invokeMethod(deviceHelper, "validateForLocation", displayablePao, lmDeviceDto);
    }

    @Test(expected = ProcessingException.class)
    public void testIsLongitudeInvalid() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);
        DisplayablePao displayablePao = new DisplayableDevice(paoIdentifier, "2879");
        LmDeviceDto lmDeviceDto = new LmDeviceDto();
        lmDeviceDto.setLatitude(70.00);
        lmDeviceDto.setLongitude(-1000.00);

        ReflectionTestUtils.invokeMethod(deviceHelper, "validateForLocation", displayablePao, lmDeviceDto);
    }

    @Test(expected = ProcessingException.class)
    public void testIsInvalidLongitude() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(2804, PaoType.LCR6700_RFN);
        DisplayablePao displayablePao = new DisplayableDevice(paoIdentifier, "2879");
        LmDeviceDto lmDeviceDto = new LmDeviceDto();
        lmDeviceDto.setLatitude(70.00);
        lmDeviceDto.setLongitude(1000.00);

        ReflectionTestUtils.invokeMethod(deviceHelper, "validateForLocation", displayablePao, lmDeviceDto);
    }

}
