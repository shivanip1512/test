package com.cannontech.common.device.service;

/**
 * Test class for PointServiceImpl
 */
import org.springframework.test.util.ReflectionTestUtils;

import junit.framework.TestCase;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.pao.service.impl.PointServiceImpl;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;

public class PointServiceImplTest extends TestCase {

    private PointService service = null;
    private SimpleDevice pao = null;

    protected void setUp() throws Exception {

        PointServiceImpl impl = new PointServiceImpl();
        ReflectionTestUtils.setField(impl, "pointDao", new MockPointDao());

        service = impl;

        pao = new SimpleDevice(1,PaoType.MCT310);
    }

    /**
     * Test getPointForDevice()
     */
    public void testGetPointForDevice() {

        // Test for point that exists for the device
        LitePoint expectedPoint = new LitePoint(1, "analog1", 1, 1, 1, 0, 0, 1);

        PointIdentifier testTemplate = new PointIdentifier(PointType.getForId(1), 0);
        LitePoint actualPoint = service.getPointForPao(pao, testTemplate);

        this.compareLitePoints("Existing point for device:", expectedPoint, actualPoint);

        // Test for point that doesn't exist for the device
        testTemplate = new PointIdentifier(PointType.getForId(4), 0);
        try {
            service.getPointForPao(pao, testTemplate);
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
        PointIdentifier testTemplate = new PointIdentifier(PointType.getForId(1), 0);
        assertTrue("The point should exist", service.pointExistsForPao(pao, testTemplate));

        // Test for point that doesn't exist for the device
        testTemplate = new PointIdentifier(PointType.getForId(4), 0);
        assertTrue("The point should not exist",
                   !service.pointExistsForPao(pao, testTemplate));
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
