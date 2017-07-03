package com.cannontech.common.pao.attribute.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.service.impl.PointServiceImpl;
import com.cannontech.database.data.lite.LitePoint;

public class AttributeServiceImplTest {

    private AttributeServiceImpl service = null;
    private PaoDefinitionDao paoDefinitionDao = null;
    private MockPointDao pointDao = null;
    private SimpleDevice device = null;

    @Before
    public void setUp() {
        service = new AttributeServiceImpl();
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
        ReflectionTestUtils.setField(service, "paoDefinitionDao", paoDefinitionDao);
        PointServiceImpl pointService = new PointServiceImpl();
        pointDao = new MockPointDao();
        ReflectionTestUtils.setField(pointService, "pointDao", pointDao);
        ReflectionTestUtils.setField(service, "pointService", pointService);
        device = new SimpleDevice(1, 1019);
    }

    @Test
    public void testGetPointForAttribute() {
        // Test for existing device / attribute
        LitePoint expectedPoint = pointDao.getLitePoint(1);
        LitePoint actualPoint = service.getPointForAttribute(device, BuiltInAttribute.USAGE);
        assertEquals("Attribute point isn't as expected", expectedPoint, actualPoint);
    }

    @Test
    public void testGetAllExistingAtributes() {
        // Test for existing device with existing attributes
        Set<Attribute> expectedAtributes = new HashSet<Attribute>();
        expectedAtributes.add(BuiltInAttribute.OUTAGE_STATUS);
        expectedAtributes.add(BuiltInAttribute.BLINK_COUNT);
        expectedAtributes.add(BuiltInAttribute.USAGE);
        expectedAtributes.add(BuiltInAttribute.COMM_STATUS);
        Set<Attribute> actualAtributes = service.getExistingAttributes(device, expectedAtributes);
        assertEquals("Existing attributes aren't as expected", expectedAtributes, actualAtributes);

        // Test for device with no attributes
        expectedAtributes = new HashSet<Attribute>();
        device = new SimpleDevice(1, 1036);
        actualAtributes = service.getExistingAttributes(device, expectedAtributes);
        assertEquals("There shouldn't be any attributes", expectedAtributes, actualAtributes);

        // Test with invalid device
        try {
            SimpleDevice invalidDevice = new SimpleDevice(1, 99);
            fail("Exception - An invalid device : " + invalidDevice);
        } catch (Exception e) {
            // expected an exception
        }
    }

    /**
     * Test BuiltInAttribute Enum values if matched with points.xml
     */
    @Test
    public void testValidateAllPointMessages() {

        Properties prop = new Properties();
        List<String> missingKeyList = new ArrayList<String>();
        String path = (AttributeServiceImplTest.class.getProtectionDomain().getCodeSource().getLocation()).toString();

        path = path.replace("/bin/", "/");
        path = path.substring(0, path.indexOf("common") + 6);
        path = path.replace("file:/", "");
        try {
            FileInputStream fis =
                new FileInputStream(path + "\\i18n\\en_US\\com\\cannontech\\yukon\\common\\point.xml");
            prop.loadFromXML(fis);
            for (BuiltInAttribute key : BuiltInAttribute.values()) {
                String keyStr = "yukon.common.attribute.builtInAttribute." + key.name();
                if (!prop.containsKey(keyStr)) {
                    missingKeyList.add(key.name());
                }
            }
        } catch (IOException io) {
            fail("caught exception in testValidateAllPointMessages" + io.getMessage());
        } catch (Exception e) {
            fail("caught exception in testValidateAllPointMessages" + e.getMessage());
        }
        assertFalse(" Message missing for keys -" + missingKeyList, missingKeyList.size() > 0);
    }

}
