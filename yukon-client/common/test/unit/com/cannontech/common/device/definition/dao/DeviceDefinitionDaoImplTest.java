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
import com.cannontech.common.mock.MockDevice;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

/**
 * Test class for DeviceDefinitionDao
 */
public class DeviceDefinitionDaoImplTest extends TestCase {

    private DeviceDefinitionDao dao = null;
    private DeviceBase device = null;

    public static DeviceDefinitionDao getTestDeviceDefinitionDao() throws Exception {

        DeviceDefinitionDaoImpl dao = new DeviceDefinitionDaoImpl();

        // Use testDeviceDefinition.xml for testing
        ((DeviceDefinitionDaoImpl) dao).setInputFile(dao.getClass()
                                                        .getClassLoader()
                                                        .getResourceAsStream("com/cannontech/common/device/definition/dao/testDeviceDefinition.xml"));
        ((DeviceDefinitionDaoImpl) dao).setPaoGroupsWrapper(new DeviceDefinitionDaoImplTest().new MockPaoGroups());
        ((DeviceDefinitionDaoImpl) dao).setJavaConstantClassName(MockPaoGroups.class.getName());
        ((DeviceDefinitionDaoImpl) dao).initialize();

        return dao;
    }

    protected void setUp() throws Exception {

        dao = DeviceDefinitionDaoImplTest.getTestDeviceDefinitionDao();

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
        Set<PointTemplate> expectedTemplates = this.getExpectedAllTemplates();

        Set<PointTemplate> actualTemplates = dao.getAllPointTemplates(device);

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates = dao.getAllPointTemplates(dao.getDeviceDefinition(device));
        assertEquals("Expected definition templates did not match device templates",
                     this.getSortedList(actualTemplates),
                     this.getSortedList(acutalDefinitionTemplates));

        assertEquals("Expected all point templates did not match: ",
                     this.getSortedList(expectedTemplates),
                     this.getSortedList(actualTemplates));

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
        Set<PointTemplate> expectedTemplates = DeviceDefinitionDaoImplTest.getExpectedInitTemplates();
        Set<PointTemplate> actualTemplates = dao.getInitPointTemplates(device);

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates = dao.getInitPointTemplates(dao.getDeviceDefinition(device));
        assertEquals("Expected definition templates did not match device templates",
                     this.getSortedList(actualTemplates),
                     this.getSortedList(acutalDefinitionTemplates));

        assertEquals("Expected init point templates did not match: ",
                     this.getSortedList(expectedTemplates),
                     this.getSortedList(actualTemplates));

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
     * Test getDeviceDefinition()
     */
    public void testGetDeviceDefinition() {

        // Test with supported device type
        DeviceDefinition expectedDefinition = new DeviceDefinition(1,
                                                                   "Device 1",
                                                                   "display1",
                                                                   "constant1",
                                                                   "change1");
        assertEquals("device1 definition is not as expected",
                     expectedDefinition,
                     dao.getDeviceDefinition(device));

        // Test with unsupported device type
        try {
            device.setDeviceType("invalid");
            dao.getDeviceDefinition(device);
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
    public void testGetDevicesForChangeGroup() {

        // Test with supported change group
        Set<DeviceDefinition> expectedDeviceTypesList = new HashSet<DeviceDefinition>();
        expectedDeviceTypesList.add(new DeviceDefinition(1,
                                                         "Device 1",
                                                         "display1",
                                                         "constant1",
                                                         "change1"));
        expectedDeviceTypesList.add(new DeviceDefinition(2,
                                                         "Device 2",
                                                         "display1",
                                                         "constant2",
                                                         "change1"));

        Set<DeviceDefinition> actualDeviceTypesList = dao.getDevicesForChangeGroup("change1");

        assertEquals("Incorrect device type list for change group: change1",
                     expectedDeviceTypesList,
                     actualDeviceTypesList);

        // Test with invalid change group
        try {
            dao.getDevicesForChangeGroup("invalid");
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
    public static Set<PointTemplate> getExpectedInitTemplates() {
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
        Set<PointTemplate> expectedTemplates = DeviceDefinitionDaoImplTest.getExpectedInitTemplates();

        // Add the rest of the templates

        // Status
        expectedTemplates.add(new PointTemplate("status1", 0, 1, 1.0, -1, 0, false, null));

        return expectedTemplates;
    }

    /**
     * Helper method to turn a set into a sorted list - This method should be
     * used to more easily see the differences between to sets if a test fails
     * @param set - Set to get list for
     * @return A sorted list containing ever object that was in the set
     */
    private List<PointTemplate> getSortedList(Set<PointTemplate> set) {
        List<PointTemplate> list = new ArrayList<PointTemplate>();
        list.addAll(set);
        Collections.sort(list);

        return list;
    }

    /**
     * Mock PaoGroups class for testing purposes. This class is public because
     * outside classes need access to the constants. The methods in this class
     * return static value expected for unit testing
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

            throw new IllegalArgumentException("Device type '" + typeString
                    + "' is not supported for testing");
        }

        public String getPAOTypeString(int type) {
            return null;
        }

    }
}
