package com.cannontech.common.device.service;

/**
 * Test class for PointServiceImpl
 */
import junit.framework.TestCase;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.incrementer.NextValueHelper;

public class PointServiceImplTest extends TestCase {

    private PointService service = null;
    private YukonDevice device = null;

    protected void setUp() throws Exception {

        PointServiceImpl impl = new PointServiceImpl();
        impl.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        impl.setPointDao(new MockPointDao());

        service = impl;

        device = new YukonDevice(1,1);
    }

    /**
     * Test getPointForDevice()
     */
    public void testGetPointForDevice() {

        // Test for point that exists for the device
        LitePoint expectedPoint = new LitePoint(1, "analog1", 1, 1, 1, 0, 0, 1);

        DevicePointIdentifier testTemplate = new DevicePointIdentifier(1, 0);
        LitePoint actualPoint = service.getPointForDevice(device, testTemplate);

        this.compareLitePoints("Existing point for device:", expectedPoint, actualPoint);

        // Test for point that doesn't exist for the device
        testTemplate = new DevicePointIdentifier(4, 0);
        try {
            service.getPointForDevice(device, testTemplate);
        } catch (NotFoundException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test pointExistsForDevice()
     */
    public void testPointExistsForDevice() {

        // Test for point that exists for the device
        DevicePointIdentifier testTemplate = new DevicePointIdentifier(1, 0);
        assertTrue("The point should exist", service.pointExistsForDevice(device, testTemplate));

        // Test for point that doesn't exist for the device
        testTemplate = new DevicePointIdentifier(4, 0);
        assertTrue("The point should not exist",
                   !service.pointExistsForDevice(device, testTemplate));
    }

    /**
     * Helper method to compare 2 lite points - this method is neccessary
     * because LitePoint.equals() only compares ids
     * @param expected
     * @param actual
     * @return True if the points are equal
     */
    private void compareLitePoints(String testName, LitePoint expected, LitePoint actual) {

        assertEquals(testName + " Point name incorrect",
                     expected.getPointName(),
                     actual.getPointName());
        assertEquals(testName + " Point offset incorrect",
                     expected.getPointOffset(),
                     actual.getPointOffset());
        assertEquals(testName + " Point type incorrect",
                     expected.getPointType(),
                     actual.getPointType());
        assertEquals(testName + " Unit of measure incorrect",
                     expected.getUofmID(),
                     actual.getUofmID());
        assertEquals(testName + " State group incorrect",
                     expected.getStateGroupID(),
                     actual.getStateGroupID());

    }

}
