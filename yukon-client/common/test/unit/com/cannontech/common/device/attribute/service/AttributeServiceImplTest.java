package com.cannontech.common.device.attribute.service;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.model.UserDefinedAttribute;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDaoImplTest;
import com.cannontech.common.device.service.PointServiceImpl;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.incrementer.NextValueHelper;

public class AttributeServiceImplTest extends TestCase {

    private AttributeServiceImpl service = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private MockPointDao pointDao = null;
    private YukonDevice device = null;

    protected void setUp() throws Exception {

        service = new AttributeServiceImpl();

        deviceDefinitionDao = DeviceDefinitionDaoImplTest.getTestDeviceDefinitionDao();
        service.setDeviceDefinitionDao(deviceDefinitionDao);

        PointServiceImpl pointService = new PointServiceImpl();
        pointService.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        pointDao = new MockPointDao();
        pointService.setPointDao(pointDao);
        service.setPointDao(pointDao);
        service.setPointService(pointService);

        device = new YukonDevice(1, 1019);
        device.setType(1019);

    }

    /**
     * Test getPointForAttribute()
     */
    public void testGetPointForAttribute() {

        // Test for existing device / attribute
        LitePoint expectedPoint = pointDao.getLitePoint(1);
        LitePoint actualPoint = service.getPointForAttribute(device, BuiltInAttribute.USAGE);

        assertEquals("Attribute point isn't as expected", expectedPoint, actualPoint);

        // Test for device that doesn't exist
        try {
            device.setType(-1);
            service.getPointForAttribute(device, new UserDefinedAttribute("invalid"));
            fail("getPointForAttribute should've thrown exception for invalid device");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

        // Test for attribute that doesn't exist
        try {
            service.getPointForAttribute(device, new UserDefinedAttribute("invalid"));
            fail("getPointForAttribute should've thrown exception for invalid attribute");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test getAllExistingAtributes()
     */
    public void testGetAllExistingAtributes() {

        // Test for existing device with existing attributes
        Set<Attribute> expectedAtributes = new HashSet<Attribute>();
        expectedAtributes.add(BuiltInAttribute.USAGE);
        expectedAtributes.add(BuiltInAttribute.DEMAND);
        expectedAtributes.add(BuiltInAttribute.LOAD_PROFILE);

        Set<Attribute> actualAtributes = service.getAllExistingAtributes(device);

        assertEquals("Existing attributes aren't as expected", expectedAtributes, actualAtributes);

        // Test for device with no attributes
        expectedAtributes = new HashSet<Attribute>();

        device.setType(1036);
        actualAtributes = service.getAllExistingAtributes(device);

        assertEquals("There shouldn't be any attributes", expectedAtributes, actualAtributes);

        // Test with invalid device
        try {
            device.setType(-1);
            actualAtributes = service.getAllExistingAtributes(device);
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }
}
