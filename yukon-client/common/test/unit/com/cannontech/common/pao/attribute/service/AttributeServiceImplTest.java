package com.cannontech.common.pao.attribute.service;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.service.PointServiceImpl;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.incrementer.NextValueHelper;

public class AttributeServiceImplTest extends TestCase {

    private AttributeServiceImpl service = null;
    private PaoDefinitionDao paoDefinitionDao = null;
    private MockPointDao pointDao = null;
    private SimpleDevice device = null;

    protected void setUp() throws Exception {

        service = new AttributeServiceImpl();

        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
        service.setPaoDefinitionDao(paoDefinitionDao);

        PointServiceImpl pointService = new PointServiceImpl();
        pointService.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        pointDao = new MockPointDao();
        pointService.setPointDao(pointDao);
        service.setPointService(pointService);

        device = new SimpleDevice(1, 1019);
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

        Set<Attribute> actualAtributes = service.getAllExistingAttributes(device);

        assertEquals("Existing attributes aren't as expected", expectedAtributes, actualAtributes);

        // Test for device with no attributes
        expectedAtributes = new HashSet<Attribute>();

        device.setType(1036);
        actualAtributes = service.getAllExistingAttributes(device);

        assertEquals("There shouldn't be any attributes", expectedAtributes, actualAtributes);

        // Test with invalid device
        try {
            device.setType(-1);
            actualAtributes = service.getAllExistingAttributes(device);
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }
}
