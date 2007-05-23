package com.cannontech.common.device.service;

/**
 * Test class for PointServiceImpl
 */
import junit.framework.TestCase;

import com.cannontech.common.device.definition.model.PointTemplateImpl;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;

public class PointServiceImplTest extends TestCase {

    private PointService service = null;
    private LiteYukonPAObject device = null;

    protected void setUp() throws Exception {

        PointServiceImpl impl = new PointServiceImpl();
        impl.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        impl.setPointDao(new MockPointDao());

        service = impl;

        device = new LiteYukonPAObject(1);
        device.setType(1);
        device.setPaoName("Test Device");
    }

    /**
     * Test getPointForDevice()
     */
    public void testGetPointForDevice() {

        // Test for point that exists for the device
        LitePoint expectedPoint = new LitePoint(1, "analog1", 1, 1, 1, 0, 0, 1);

        PointTemplateImpl testTemplate = new PointTemplateImpl("test", 1, 0, 0.0, 0, 0, false, null);
        LitePoint actualPoint = service.getPointForDevice(device, testTemplate);

        this.compareLitePoints("Existing point for device:", expectedPoint, actualPoint);

        // Test for point that doesn't exist for the device
        testTemplate.setType(4);
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
        PointTemplateImpl testTemplate = new PointTemplateImpl("test", 1, 0, 0.0, 0, 0, false, null);
        assertTrue("The point should exist", service.pointExistsForDevice(device, testTemplate));

        // Test for point that doesn't exist for the device
        testTemplate.setType(4);
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
