package com.cannontech.common.device.definition.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.CommandDefinitionImpl;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.PointReference;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.PointTemplateImpl;
import com.cannontech.common.mock.MockDevice;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
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
        ((DeviceDefinitionDaoImpl) dao).setStateDao(new DeviceDefinitionDaoImplTest().new MockStateDao());
        ((DeviceDefinitionDaoImpl) dao).setUnitMeasureDao(new DeviceDefinitionDaoImplTest().new MockUnitMeasureDao());
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
        expectedAttributes.add(new Attribute("totalUsage"));
        expectedAttributes.add(new Attribute("pulse2"));

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
        PointTemplate expectedTemplate = new PointTemplateImpl("pulse1",
                                                               2,
                                                               2,
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
            dao.getPointTemplateForAttribute(device, new Attribute("invalid"));
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
        DeviceDefinition expectedDefinition = new DeviceDefinitionImpl(1,
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
        expectedDeviceTypesList.add(new DeviceDefinitionImpl(1,
                                                             "Device 1",
                                                             "display1",
                                                             "constant1",
                                                             "change1"));
        expectedDeviceTypesList.add(new DeviceDefinitionImpl(2,
                                                             "Device 2",
                                                             "display1",
                                                             "constant2",
                                                             "change1"));

        DeviceDefinitionImpl definition = new DeviceDefinitionImpl(1,
                                                                   "test",
                                                                   "test",
                                                                   "test",
                                                                   "change1");
        Set<DeviceDefinition> actualDeviceTypesList = dao.getChangeableDevices(definition);

        assertEquals("Incorrect device type list for change group: change1",
                     expectedDeviceTypesList,
                     actualDeviceTypesList);

        // Test with invalid change group
        try {
            definition.setChangeGroup("invalid");
            dao.getChangeableDevices(definition);
            fail("Exception should be thrown for invalid paoClass");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test getAffected()
     */
    public void testGetAffected() {

        Set<PointTemplate> points = new HashSet<PointTemplate>();
        Set<CommandDefinition> expectedCommandSet = new HashSet<CommandDefinition>();

        // Define expected points
        PointTemplateImpl point0 = new PointTemplateImpl("status1", 0, 0, 0.0, 0, 0, false, null);
        PointTemplateImpl point1 = new PointTemplateImpl("pulse1",
                                                         2,
                                                         2,
                                                         1.0,
                                                         1,
                                                         0,
                                                         true,
                                                         Attribute.USAGE);
        PointTemplateImpl point2 = new PointTemplateImpl("pulse2",
                                                         2,
                                                         4,
                                                         1.0,
                                                         1,
                                                         0,
                                                         false,
                                                         new Attribute("pulse2"));

        // Define expected command definitions
        CommandDefinitionImpl command1 = new CommandDefinitionImpl();
        command1.addCommandString("do command1");
        command1.addAffectedPoint(new PointReference("pulse1"));

        CommandDefinitionImpl command2 = new CommandDefinitionImpl();
        command2.addCommandString("do command2");
        command2.addCommandString("continue command2");
        command2.addAffectedPoint(new PointReference("pulse1"));
        command2.addAffectedPoint(new PointReference("pulse2"));

        
        // Test with no expected commands
        points.add(point0);
        Set<CommandDefinition> actualCommands = dao.getAffected(device, points);
        assertEquals("Expected no commands", expectedCommandSet, actualCommands);

        // Test with one expected command
        points.clear();
        points.add(point2);
        expectedCommandSet.add(command2);

        actualCommands = dao.getAffected(device, points);
        assertEquals("Expected 1 command", expectedCommandSet, actualCommands);

        // Test with two expected commands
        points.clear();
        points.add(point1);
        expectedCommandSet.add(command1);

        actualCommands = dao.getAffected(device, points);
        assertEquals("Expected 2 commands", expectedCommandSet, actualCommands);

    }

    /**
     * Helper method to get the set of init point templates for device1
     * @return Set of all point templates
     */
    public static Set<PointTemplate> getExpectedInitTemplates() {
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplateImpl("pulse1",
                                                    2,
                                                    2,
                                                    1.0,
                                                    1,
                                                    0,
                                                    true,
                                                    Attribute.USAGE));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplateImpl("demand1",
                                                    3,
                                                    1,
                                                    1.0,
                                                    0,
                                                    0,
                                                    true,
                                                    Attribute.DEMAND));

        // Analog
        expectedTemplates.add(new PointTemplateImpl("analog1",
                                                    1,
                                                    1,
                                                    1.0,
                                                    1,
                                                    0,
                                                    true,
                                                    new Attribute("totalUsage")));

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

        // Pulse Accumulator
        expectedTemplates.add(new PointTemplateImpl("pulse2",
                                                    2,
                                                    4,
                                                    1.0,
                                                    1,
                                                    0,
                                                    false,
                                                    new Attribute("pulse2")));

        // Status
        expectedTemplates.add(new PointTemplateImpl("status1", 0, 1, 1.0, -1, 0, false, null));

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

    private class MockUnitMeasureDao implements UnitMeasureDao {

        public List<LiteUnitMeasure> getLiteUnitMeasures() {
            return null;
        }

        public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
            return null;
        }

        public LiteUnitMeasure getLiteUnitMeasure(int uomid) {
            return null;
        }

        public LiteUnitMeasure getLiteUnitMeasure(String uomName) {

            if (uomName.equals("measure0")) {
                return new LiteUnitMeasure(0, uomName, 0, uomName);
            } else if (uomName.equals("measure1")) {
                return new LiteUnitMeasure(1, uomName, 0, uomName);
            }
            throw new IllegalArgumentException("Unit of measure doesn't exist: " + uomName);
        }

    }

    private class MockStateDao implements StateDao {

        public LiteState getLiteState(int stateGroupID, int rawState) {
            return null;
        }

        public LiteStateGroup getLiteStateGroup(int stateGroupID) {
            return null;
        }

        public LiteStateGroup getLiteStateGroup(String stateGroupName) {

            if (stateGroupName.equals("state0")) {
                return new LiteStateGroup(0);
            }
            throw new IllegalArgumentException("State group doesn't exist: " + stateGroupName);
        }

        public LiteState[] getLiteStates(int stateGroupID) {
            return null;
        }

        public LiteStateGroup[] getAllStateGroups() {
            return null;
        }

    }
}
