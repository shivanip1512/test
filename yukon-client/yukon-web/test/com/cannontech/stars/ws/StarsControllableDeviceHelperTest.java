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

}
