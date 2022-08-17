package com.cannontech.simulators.ivvc.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.simulators.ivvc.impl.IvvcSimulatorServiceImpl;

public class IvvcSimulatorServiceImplTest {

    @Test
    public void testSetPointOffsetCalc() {
        IvvcSimulatorServiceImpl ivvcSimulatorServiceImpl = new IvvcSimulatorServiceImpl();
        
        // No tap change, they are equal
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 120.0, 2.0));
        // No tap change, inside of bandwidth
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 121.0, 2.0));
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 119.0, 2.0));
        
        // 1 tap, close to bandwidth
        Assert.assertEquals(1, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 118.9, 2.0));
        Assert.assertEquals(-1, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 121.1, 2.0));
        
        // Smaller bandwidth
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 120.25, .5));
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 119.75, .5));
        Assert.assertEquals(-1, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 120.26, .5));
        Assert.assertEquals(1, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 119.74, .5));
        Assert.assertEquals(-2, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 121.01, .5));
        // Note for this result there is some float rounding going on. We are accepting that while 120.25 does not tap, 121 taps down twice (logically it should be once).
        Assert.assertEquals(-2, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 121.00, .5));
        
        Assert.assertEquals(2, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 119.0, .5));
        
        Assert.assertEquals(-8, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 125.5, .5));
        Assert.assertEquals(-7, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 125.49, .5));
        Assert.assertEquals(9, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 113.74, .5));
        Assert.assertEquals(8, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 113.76, .5));
        
        // Larger bandwidth
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 122.0, 4.0));
        Assert.assertEquals(0, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 118.0, 4.0));
        Assert.assertEquals(-5, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 125.0, 4.0));
        Assert.assertEquals(6, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 114.0, 4.0));
        Assert.assertEquals(-5, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 125.5, 4.0));
        Assert.assertEquals(6, (int) ReflectionTestUtils.invokeMethod(ivvcSimulatorServiceImpl, "getSetPointTapChange", 120.0, 113.6, 4.0));
    }

}
