package com.cannontech.common.device.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Test class for PointServiceImpl
 */
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.pao.service.impl.PointServiceImpl;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;

public class PointServiceImplTest {

    private PointService service = null;
    private SimpleDevice pao = null;

    @BeforeEach
    protected void setUp() throws Exception {

        PointServiceImpl impl = new PointServiceImpl();
        ReflectionTestUtils.setField(impl, "pointDao", new MockPointDao());

        service = impl;

        pao = new SimpleDevice(1,PaoType.MCT310);
    }

    /**
     * Test getPointForDevice()
     */
    @Test
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
    @Test
    public void testPointExistsForDevice() {

        // Test for point that exists for the device
        PointIdentifier testTemplate = new PointIdentifier(PointType.getForId(1), 0);
        assertTrue(service.pointExistsForPao(pao, testTemplate), "The point should exist");

        // Test for point that doesn't exist for the device
        testTemplate = new PointIdentifier(PointType.getForId(4), 0);
        assertTrue(!service.pointExistsForPao(pao, testTemplate), "The point should not exist");
    }

    /**
     * Helper method to compare 2 lite points - this method is neccessary
     * because LitePoint.equals() only compares ids
     * @param expected
     * @param actual
     * @return True if the points are equal
     */
    private void compareLitePoints(String testName, LitePoint expected, LitePoint actual) {

        assertEquals(expected.getPointName(), actual.getPointName(), testName + " Point name incorrect");
        assertEquals(expected.getPointOffset(), actual.getPointOffset(), testName + " Point offset incorrect");
        assertEquals(expected.getPointType(), actual.getPointType(), testName + " Point type incorrect");
        assertEquals(expected.getUofmID(), actual.getUofmID(), testName + " Unit of measure incorrect");
        assertEquals(expected.getStateGroupID(), actual.getStateGroupID(), testName + " State group incorrect");
    }

}
