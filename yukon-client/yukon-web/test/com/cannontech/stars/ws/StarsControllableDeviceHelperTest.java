package com.cannontech.stars.ws;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
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
        assertFalse(isOperationAllowed, "Is operation allowed");
    }

    @Test
    public void testIsOperationAllowedForDeviceNonNest() {
        boolean isOperationAllowed =
            ReflectionTestUtils.invokeMethod(deviceHelper, "isOperationAllowedForHardware", HardwareType.HONEYWELL_9000);
        assertTrue(isOperationAllowed, "Is operation allowed");
    }

}
