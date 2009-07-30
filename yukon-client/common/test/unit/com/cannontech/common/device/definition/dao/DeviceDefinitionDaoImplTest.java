package com.cannontech.common.device.definition.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.core.io.UrlResource;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.DevicePointTemplate;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Test class for DeviceDefinitionDao
 */
public class DeviceDefinitionDaoImplTest extends TestCase {

    private DeviceDefinitionDao dao = null;
    private SimpleDevice device = null;

    public static DeviceDefinitionDao getTestDeviceDefinitionDao() throws Exception {

        DeviceDefinitionDaoImpl dao = new DeviceDefinitionDaoImpl();

        // Use testDeviceDefinition.xml for testing
        ClassLoader classLoader = dao.getClass().getClassLoader();
        URL inputResource = classLoader.getResource("com/cannontech/common/device/definition/dao/testDeviceDefinition.xml");
        dao.setInputFile(new UrlResource(inputResource));

        URL schemaResource = classLoader.getResource("com/cannontech/common/device/definition/dao/deviceDefinition.xsd");
        dao.setSchemaFile(new UrlResource(schemaResource));

        dao.setCustomInputFile(null);
        dao.setStateDao(new DeviceDefinitionDaoImplTest().new MockStateDao());
        dao.setUnitMeasureDao(new DeviceDefinitionDaoImplTest().new MockUnitMeasureDao());
        dao.initialize();

        return dao;
    }

    protected void setUp() throws Exception {

        dao = DeviceDefinitionDaoImplTest.getTestDeviceDefinitionDao();

        device = new SimpleDevice(10, 1019);
        device.setType(1019);
    }
    
    public void assertAttributeLookupsMatch(String message, Set<Attribute> attributes, Set<AttributeDefinition> lookups) {
        Builder<Attribute> builder = ImmutableSet.builder();
        for (AttributeDefinition lookup : lookups) {
            builder.add(lookup.getAttribute());
        }
        ImmutableSet<Attribute> attributesFromLookups = builder.build();
        assertEquals(message, attributes, attributesFromLookups);
    }

    /**
     * Test getAvailableAttributes()
     */
    public void testGetDefinedAttributes() {

        // Test with supported device type
        Set<Attribute> expectedAttributes = new HashSet<Attribute>();
        expectedAttributes.add(BuiltInAttribute.USAGE);
        expectedAttributes.add(BuiltInAttribute.DEMAND);
        expectedAttributes.add(BuiltInAttribute.LOAD_PROFILE);

        Set<AttributeDefinition> actualAttributes = dao.getDefinedAttributes(device.getDeviceType());

        assertAttributeLookupsMatch("Expected attributes did not match: ", expectedAttributes, actualAttributes);

    }

    public void testGetPointTemplateForAttribute() {

        // Test with supported device type
        PointTemplate expectedPointTemplate = new PointTemplate("pulse1",
                                                               2,
                                                               2,
                                                               1.0,
                                                               1,
                                                               0);
        DevicePointTemplate expectedDevicePointTemplate = new DevicePointTemplate(device, expectedPointTemplate);

        AttributeDefinition attributeDefinition = dao.getAttributeLookup(device.getDeviceType(), BuiltInAttribute.USAGE);
        DevicePointTemplate actualDevicePointTemplate = attributeDefinition.getPointTemplate(device);
        
        assertEquals("Expected point template did not match: ", expectedDevicePointTemplate, actualDevicePointTemplate);

    }

    /**
     * Test getAllPointTemplates()
     */
    public void testGetAllPointTemplates() {

        // Test with supported device type
        Set<PointTemplate> expectedTemplates = this.getExpectedAllTemplates();

        Set<PointTemplate> actualTemplates = dao.getAllPointTemplates(device.getDeviceType());

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates = dao.getAllPointTemplates(dao.getDeviceDefinition(device.getDeviceType()));
        assertEquals("Expected definition templates did not match device templates",
                     this.getSortedList(actualTemplates),
                     this.getSortedList(acutalDefinitionTemplates));

        assertEquals("Expected all point templates did not match: ",
                     this.getSortedList(expectedTemplates),
                     this.getSortedList(actualTemplates));

    }

    /**
     * Test getInitPointTemplates()
     */
    public void testGetInitPointTemplates() {

        // Test with supported device type
        Set<PointTemplate> expectedTemplates = DeviceDefinitionDaoImplTest.getExpectedInitTemplates();
        Set<PointTemplate> actualTemplates = dao.getInitPointTemplates(device.getDeviceType());

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates = dao.getInitPointTemplates(dao.getDeviceDefinition(device.getDeviceType()));
        assertEquals("Expected definition templates did not match device templates",
                     this.getSortedList(actualTemplates),
                     this.getSortedList(acutalDefinitionTemplates));

        assertEquals("Expected init point templates did not match: ",
                     this.getSortedList(expectedTemplates),
                     this.getSortedList(actualTemplates));

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
        DeviceDefinition expectedDefinition = new DeviceDefinitionImpl(PaoType.getForId(1019),
                                                                       "Device 1",
                                                                       "display1",
                                                                       "MCT310",
                                                                       "change1");
        assertEquals("device1 definition is not as expected",
                     expectedDefinition,
                     dao.getDeviceDefinition(device.getDeviceType()));

    }

    /**
     * Test getDeviceTypesForChangeGroup()
     */
    public void testGetDevicesForChangeGroup() {

        // Test with supported change group
        Set<DeviceDefinition> expectedDeviceTypesList = new HashSet<DeviceDefinition>();
        expectedDeviceTypesList.add(new DeviceDefinitionImpl(PaoType.getForId(1019),
                                                             "Device 1",
                                                             "display1",
                                                             "MCT310",
                                                             "change1"));
        expectedDeviceTypesList.add(new DeviceDefinitionImpl(PaoType.getForId(1022),
                                                             "Device 2",
                                                             "display1",
                                                             "MCT370",
                                                             "change1"));

        DeviceDefinitionImpl definition = new DeviceDefinitionImpl(PaoType.getForId(1019),
                                                                   "test",
                                                                   "test",
                                                                   "test",
                                                                   "change1");
        Set<DeviceDefinition> actualDeviceTypesList = dao.getDevicesThatDeviceCanChangeTo(definition);

        assertEquals("Incorrect device type list for change group: change1",
                     expectedDeviceTypesList,
                     actualDeviceTypesList);

        // Test with invalid change group
        try {
            definition.setChangeGroup("invalid");
            dao.getDevicesThatDeviceCanChangeTo(definition);
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

        Set<PointIdentifier> points = new HashSet<PointIdentifier>();
        Set<CommandDefinition> expectedCommandSet = new HashSet<CommandDefinition>();

        // Define expected points
        PointIdentifier status1 = new PointIdentifier(0, 0);
        PointIdentifier pulse1 = new PointIdentifier(2, 2);
        PointIdentifier pulse2 = new PointIdentifier(2, 4);

        // Define expected command definitions
        CommandDefinition command1 = new CommandDefinition("command1");
        command1.addCommandString("do command1");
        command1.addAffectedPoint(pulse1);

        CommandDefinition command2 = new CommandDefinition("command2");
        command2.addCommandString("do command2");
        command2.addCommandString("continue command2");
        command2.addAffectedPoint(pulse1);
        command2.addAffectedPoint(pulse2);

        
        // Test with no expected commands
        points.add(status1);
        Set<CommandDefinition> actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected no commands", expectedCommandSet, actualCommands);

        // Test with one expected command
        points.clear();
        points.add(pulse2);
        expectedCommandSet.add(command2);

        actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected 1 command", expectedCommandSet, actualCommands);

        // Test with two expected commands
        points.clear();
        points.add(pulse1);
        expectedCommandSet.add(command1);

        actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected 2 commands", expectedCommandSet, actualCommands);

    }
    
    /**
     * Test Custom Definition file
     * @throws Exception 
     */
    public void testCustomDefinition() throws Exception {
        ClassLoader classLoader = dao.getClass().getClassLoader();

        // Set up the device definition dao with both an inputFile and a customInputFile
        DeviceDefinitionDaoImpl dao = new DeviceDefinitionDaoImpl();
        URL inputResource = classLoader.getResource("com/cannontech/common/device/definition/dao/testDeviceDefinition.xml");
        dao.setInputFile(new UrlResource(inputResource));
        URL customFileUrl = classLoader.getResource("com/cannontech/common/device/definition/dao/testCustomDeviceDefinition.xml");
        
        URL schemaResource = classLoader.getResource("com/cannontech/common/device/definition/dao/deviceDefinition.xsd");
        dao.setSchemaFile(new UrlResource(schemaResource));

        dao.setCustomInputFile(new UrlResource(customFileUrl));
        dao.setStateDao(new DeviceDefinitionDaoImplTest().new MockStateDao());
        dao.setUnitMeasureDao(new DeviceDefinitionDaoImplTest().new MockUnitMeasureDao());
        dao.initialize();
        
        
        // Test that the point templates are custom
        
        // Test custom definition overrides standard definition - point
        PointTemplate expectedPulse1PointTemplate = new PointTemplate("pulse1",
		                                                2,
		                                                2,
		                                                2.5,
		                                                0,
		                                                0);
        
        PaoType deviceType = PaoType.getForId(device.getType());
        PointIdentifier pointIdentifier = new PointIdentifier(2, 2);
        PointTemplate actualPulse1PointTemplate = dao.getPointTemplateByTypeAndOffset(deviceType, pointIdentifier);
        
        assertEquals("Expected point customizations do not match: ", expectedPulse1PointTemplate, actualPulse1PointTemplate);

        // Test custom definition overrides standard definition - command
        PointIdentifier pulse1 = new PointIdentifier(2, 2);
        PointIdentifier pulse2 = new PointIdentifier(2, 4);
        Set<PointIdentifier> points = new HashSet<PointIdentifier>();
        Set<CommandDefinition> expectedCommandSet = new HashSet<CommandDefinition>();
        
        CommandDefinition command1 = new CommandDefinition("command1");
        command1.addCommandString("do custom command1");
        command1.addAffectedPoint(pulse2);

        points.add(pulse2);
        expectedCommandSet.add(command1);
        
        CommandDefinition command2 = new CommandDefinition("command2");
        command2.addCommandString("do command2");
        command2.addCommandString("continue command2");
        command2.addAffectedPoint(pulse1);
        command2.addAffectedPoint(pulse2);
        
        expectedCommandSet.add(command2);

        Set<CommandDefinition> actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected command customizations do not match:", expectedCommandSet, actualCommands);
        
        // Test that device definition is custom
        
        // Test with supported device type
        device.setType(1019);
        DeviceDefinition expectedDefinition = new DeviceDefinitionImpl(PaoType.getForId(1019),
                                                                       "Custom Device 1",
                                                                       "customDisplay1",
                                                                       "MCT310",
                                                                       "customChange1");
        assertEquals("device1 definition is not as expected",
                     expectedDefinition,
                     dao.getDeviceDefinition(device.getDeviceType()));

    }
    

    /**
     * Helper method to get the set of init point templates for device1
     * @return Set of all point templates
     */
    public static Set<PointTemplate> getExpectedInitTemplates() {
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("pulse1",
                                                    2,
                                                    2,
                                                    1.0,
                                                    1,
                                                    0));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("demand1",
                                                    3,
                                                    1,
                                                    1.0,
                                                    0,
                                                    0));

        // Analog
        expectedTemplates.add(new PointTemplate("analog1",
                                                    1,
                                                    1,
                                                    1.0,
                                                    1,
                                                    0));

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
        expectedTemplates.add(new PointTemplate("pulse2",
                                                    2,
                                                    4,
                                                    1.0,
                                                    1,
                                                    0));

        // Status
        expectedTemplates.add(new PointTemplate("status1", 0, 1, 1.0, -1, 0));

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

            throw new UnsupportedOperationException();
        }

        public String getPAOTypeString(int type) {
            return Integer.toString(type);
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