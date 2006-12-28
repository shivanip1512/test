package com.cannontech.common.device.definition.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class DeviceDefinitionDaoImplTest extends TestCase {

    private DeviceDefinitionDao dao = null;
    private DeviceBase device = null;

    protected void setUp() throws Exception {
        dao = new DeviceDefinitionDaoImpl();

        // Use testDeviceDefinition.xml for testing
        ((DeviceDefinitionDaoImpl) dao).setInputFile(this.getClass()
                                                         .getClassLoader()
                                                         .getResourceAsStream("com/cannontech/common/device/definition/dao/testDeviceDefinition.xml"));
        ((DeviceDefinitionDaoImpl) dao).setPaoGroupsWrapper(new MockPaoGroups());
        ((DeviceDefinitionDaoImpl) dao).setJavaConstantClassName(MockPaoGroups.class.getName());
        ((DeviceDefinitionDaoImpl) dao).initialize();

        device = new MockDevice();
        device.setDeviceType("device1");
    }

    /**
     * Test getAvailableAttributes()
     */
    public void testGetAvailableAttributes() {

        // Test with supported device type
        Set<Attribute> expectedAttributes = new HashSet<Attribute>();
        expectedAttributes.add(Attribute.USAGE);
        expectedAttributes.add(Attribute.DEMAND);
        expectedAttributes.add(new Attribute("totalUsage", "totalUsage"));

        Set<Attribute> actualAttributes = dao.getAvailableAttributes(device);

        assertEquals("Expected attributes did not match: ", expectedAttributes, actualAttributes);

        // Test with unsupported device type
        try {
            device.setDeviceType("invalid");
            dao.getAvailableAttributes(device);
            fail("Exception should be thrown for invalid device type");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    public void testGetPointTemplateForAttribute() {

        // Test with supported device type
        PointTemplate expectedTemplate = new PointTemplate("pulse1",
                                                           2,
                                                           1,
                                                           1.0,
                                                           1,
                                                           0,
                                                           true,
                                                           Attribute.USAGE);

        PointTemplate actualTemplate = dao.getPointTemplateForAttribute(device, Attribute.USAGE);

        assertEquals("Expected point template did not match: ", expectedTemplate, actualTemplate);

        // Test with invalid attribute for the device
        try {
            device.setDeviceType("invalid");
            dao.getPointTemplateForAttribute(device, new Attribute("invalid", "invalid"));
            fail("Exception should be thrown for invalid device type");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

        // Test with unsupported device type
        try {
            device.setDeviceType("invalid");
            dao.getPointTemplateForAttribute(device, Attribute.USAGE);
            fail("Exception should be thrown for invalid device type");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test getAllPointTemplates()
     */
    public void testGetAllPointTemplates() {

        // Test with supported device type
        // Sort the sets into lists for ease of test trouble shooting
        Set<PointTemplate> expectedTemplates = this.getExpectedAllTemplates();
        List<PointTemplate> expected = new ArrayList<PointTemplate>();
        expected.addAll(expectedTemplates);
        Collections.sort(expected);

        Set<PointTemplate> actualTemplates = dao.getAllPointTemplates(device);
        List<PointTemplate> actual = new ArrayList<PointTemplate>();
        actual.addAll(actualTemplates);
        Collections.sort(actual);

        assertEquals("Expected all point templates did not match: ", expected, actual);

        // Test with unsupported device type
        try {
            device.setDeviceType("invalid");
            dao.getAvailableAttributes(device);
            fail("Exception should be thrown for invalid device type");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test getInitPointTemplates()
     */
    public void testGetInitPointTemplates() {
        // Test with supported device type
        Set<PointTemplate> expectedTemplates = this.getExpectedInitTemplates();
        List<PointTemplate> expected = new ArrayList<PointTemplate>();
        expected.addAll(expectedTemplates);
        Collections.sort(expected);

        Set<PointTemplate> actualTemplates = dao.getInitPointTemplates(device);
        List<PointTemplate> actual = new ArrayList<PointTemplate>();
        actual.addAll(actualTemplates);
        Collections.sort(actual);

        assertEquals("Expected init point templates did not match: ", expected, actual);

        // Test with unsupported device type
        try {
            device.setDeviceType("invalid");
            dao.getAvailableAttributes(device);
            fail("Exception should be thrown for invalid device type");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test getDeviceDisplayGroupMap()
     */
    public void testGetDeviceDisplayGroupMap() {

        Map<String, List<DeviceDefinition>> deviceDisplayGroupMap = dao.getDeviceDisplayGroupMap();

        // Make sure there are the correct number of device type groups
        assertEquals("There should be 2 device type groups", 2, deviceDisplayGroupMap.keySet()
                                                                                     .size());

        // Make sure there are the correct number of display1 device types
        try {
            assertEquals("There should be 2 display1 device types",
                         2,
                         deviceDisplayGroupMap.get("display1").size());
        } catch (NullPointerException e) {
            fail("There is not a display1 device type group");
        }

        // Make sure there are the correct number of display2 device
        // types
        try {
            assertEquals("There should be 1 display2 device types",
                         1,
                         deviceDisplayGroupMap.get("display2").size());
        } catch (NullPointerException e) {
            fail("There is not a display2 device type group");
        }

    }

    /**
     * Test isDeviceTypeChangeable()
     */
    public void testIsDeviceTypeChangeable() {

        // Test with supported device type that is changeable
        assertTrue("device1 is changeable", dao.isDeviceTypeChangeable(device));

        // Test with supported device type that is not changeable
        device.setDeviceType("device3");
        assertTrue("device3 is not changeable", !dao.isDeviceTypeChangeable(device));

        // Test with unsupported device type
        try {
            device.setDeviceType("invalid");
            dao.isDeviceTypeChangeable(device);
            fail("Exception should be thrown for invalid device type");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test getDeviceTypesForChangeGroup()
     */
    public void testGetDeviceTypesForChangeGroup() {

        // Test with supported change group
        Set<Integer> expectedDeviceTypesList = new HashSet<Integer>();
        expectedDeviceTypesList.add(MockPaoGroups.constant1);
        expectedDeviceTypesList.add(MockPaoGroups.constant2);

        Set<Integer> actualDeviceTypesList = dao.getDeviceTypesForChangeGroup("change1");

        assertEquals("Incorrect device type list for change group: change1",
                     expectedDeviceTypesList,
                     actualDeviceTypesList);

        // Test with invalid change group
        try {
            dao.getDeviceTypesForChangeGroup("invalid");
            fail("Exception should be thrown for invalid paoClass");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Helper method to get the set of init point templates for device1
     * @return Set of all point templates
     */
    private Set<PointTemplate> getExpectedInitTemplates() {
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("pulse1", 2, 1, 1.0, 1, 0, true, Attribute.USAGE));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("demand1", 3, 1, 1.0, 0, 0, true, Attribute.DEMAND));

        // Analog
        expectedTemplates.add(new PointTemplate("analog1",
                                                1,
                                                1,
                                                1.0,
                                                1,
                                                0,
                                                true,
                                                new Attribute("totalUsage", "totalUsage")));

        return expectedTemplates;

    }

    /**
     * Helper method to get the set of all point templates for device1
     * @return Set of all point templates
     */
    private Set<PointTemplate> getExpectedAllTemplates() {

        // Get the init templates first
        Set<PointTemplate> expectedTemplates = this.getExpectedInitTemplates();

        // Add the rest of the templates

        // Status
        expectedTemplates.add(new PointTemplate("status1", 0, 1, 1.0, -1, 0, false, null));

        return expectedTemplates;
    }

    /**
     * Mock device to be used for testing - was created to enable instantiation
     * of a device base
     */
    private class MockDevice extends DeviceBase {
    }

    /**
     * Mock PaoGroups class for testing purposes. This class is public because
     * outside classes need access to the constants
     */
    public class MockPaoGroups implements PaoGroupsWrapper {

        public static final int constant1 = 1;
        public static final int constant2 = 2;
        public static final int constant3 = 3;

        public int getDeviceType(String typeString) {

            if ("device1".equals(typeString)) {
                return constant1;
            } else if ("device2".equals(typeString)) {
                return constant2;
            } else if ("device3".equals(typeString)) {
                return constant3;
            }

            return 0;
        }

    }
}
