package com.cannontech.common.pao.attribute.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.service.impl.PointServiceImpl;
import com.cannontech.database.data.lite.LitePoint;

public class AttributeServiceImplTest {

    private AttributeServiceImpl service;
    private PaoDefinitionDao paoDefinitionDao;
    private MockPointDao pointDao;
    private SimpleDevice device;

    @Before
    public void setUp() {
        service = new AttributeServiceImpl();
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
        ReflectionTestUtils.setField(service, "paoDefinitionDao", paoDefinitionDao);
        PointServiceImpl pointService = new PointServiceImpl();
        pointDao = new MockPointDao();
        ReflectionTestUtils.setField(pointService, "pointDao", pointDao);
        ReflectionTestUtils.setField(service, "pointService", pointService);
        device = new SimpleDevice(1, PaoType.MCT310.getDeviceTypeId());
    }

    @Test
    public void test_findAttributesForPoint() {
        Set<Attribute> possibleMatches = new HashSet<>();
        // Note the attribute used here has no affect on this test or this method call.
        PaoTypePointIdentifier paoTypePointIdentifier = service.getPaoTypePointIdentifierForAttribute(PaoType.RFN410FL, BuiltInAttribute.USAGE);
        Set<BuiltInAttribute> foundAttributes = service.findAttributesForPoint(paoTypePointIdentifier, possibleMatches);
        
        // Its empty, I didn't provide any possible matches
        assertTrue(foundAttributes.isEmpty());
        
        possibleMatches.add(BuiltInAttribute.DNP3_ADDRESS_CHANGED);
        foundAttributes = service.findAttributesForPoint(paoTypePointIdentifier, possibleMatches);
  
        // Its empty, RFN 410FL does not have this attribute
        assertTrue(foundAttributes.isEmpty());
        
        possibleMatches.add(BuiltInAttribute.BLINK_COUNT);
        foundAttributes = service.findAttributesForPoint(paoTypePointIdentifier, possibleMatches);
        
        // Its empty, the point found with USAGE does not also have BLINK_COUNT
        assertTrue(foundAttributes.isEmpty());
        
        possibleMatches.add(BuiltInAttribute.USAGE);
        foundAttributes = service.findAttributesForPoint(paoTypePointIdentifier, possibleMatches);
        
        // Added usage, the point that we found via USAGE has USAGE!
        assertTrue(foundAttributes.contains(BuiltInAttribute.USAGE));
        assertTrue(foundAttributes.size() == 1);
    }
    
    @Test
    public void test_getPointForAttribute() {
        // Test for existing device / attribute
        LitePoint expectedPoint = pointDao.getLitePoint(1);
        LitePoint actualPoint = service.getPointForAttribute(device, BuiltInAttribute.USAGE);
        assertEquals("Attribute point isn't as expected", expectedPoint, actualPoint);
    }

    @Test
    public void test_getAllExistingAttributes_deviceWithAttributes() {
        Set<Attribute> expectedAttributes = new HashSet<>();
        expectedAttributes.add(BuiltInAttribute.OUTAGE_STATUS);
        expectedAttributes.add(BuiltInAttribute.BLINK_COUNT);
        expectedAttributes.add(BuiltInAttribute.USAGE);
        expectedAttributes.add(BuiltInAttribute.COMM_STATUS);
        Set<Attribute> actualAttributes = service.getExistingAttributes(device, expectedAttributes);
        assertEquals("Existing attributes aren't as expected", expectedAttributes, actualAttributes);
    }

    @Test
    public void test_getAllExistingAttributes_deviceWithNoAttributes() {
        Set<Attribute> expectedAttributes = new HashSet<>();
        device = new SimpleDevice(1, PaoType.MCT318L.getDeviceTypeId());
        Set<Attribute> actualAttributes = service.getExistingAttributes(device, expectedAttributes);
        assertEquals("There shouldn't be any attributes", expectedAttributes, actualAttributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAllExistingAttributes_withInvalidDevice() {
        SimpleDevice invalidDevice = new SimpleDevice(1, 99);
    }

    /**
     * Test BuiltInAttribute Enum values if matched with points.xml
     */
    @Test
    public void test_validateAllPointMessages() {

        Properties prop = new Properties();
        List<String> missingKeyList = new ArrayList<>();
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
