package com.cannontech.stars.ws;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.inventory.HardwareType;


public class StarsControllableDeviceHelperTest {
    StarsControllableDeviceHelperImpl deviceHelper = new StarsControllableDeviceHelperImpl();

    @Test
    public void testIsOperationAllowedForDeviceNest() {
        boolean isOperationAllowed =
            ReflectionTestUtils.invokeMethod(deviceHelper, "isOperationAllowedForHardware", HardwareType.NEST_THERMOSTAT);
        assertTrue("Is operation allowed", !isOperationAllowed);
    }

    @Test
    public void testIsOperationAllowedForDeviceNonNest() {
        boolean isOperationAllowed =
            ReflectionTestUtils.invokeMethod(deviceHelper, "isOperationAllowedForHardware", HardwareType.HONEYWELL_9000);
        assertTrue("Is operation allowed", isOperationAllowed);
    }
}
