package com.cannontech.common.pao.definition.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.cannontech.common.config.ConfigResourceLoader;
import com.cannontech.common.config.retrieve.ConfigFile;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinitionImpl;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Test class for PaoDefinitionDao
 */
public class PaoDefinitionDaoImplTest {

    private PaoDefinitionDao dao = null;
    private SimpleDevice device = null;

    public static PaoDefinitionDao getTestPaoDefinitionDao(ConfigResourceLoader loader) throws Exception {

        PaoDefinitionDaoImpl dao = new PaoDefinitionDaoImpl();
        dao.setconfigResourceLoader(loader);

        // Use testPaoDefinition.xml for testing
        ClassLoader classLoader = dao.getClass().getClassLoader();
        URL inputResource = classLoader.getResource("com/cannontech/common/pao/definition/dao/testPaoDefinition.xml");
        dao.setInputFile(new UrlResource(inputResource));

        URL schemaResource = classLoader.getResource("com/cannontech/common/pao/definition/dao/paoDefinition.xsd");
        dao.setSchemaFile(new UrlResource(schemaResource));
        
        // setCustomInputFile MUST be called BEFORE initialize
        dao.setCustomInputFile(null);
        dao.setStateDao(new PaoDefinitionDaoImplTest().new MockStateDao());
        dao.setUnitMeasureDao(new PaoDefinitionDaoImplTest().new MockUnitMeasureDao());
        dao.initialize();

        return dao;
    }

    @Before
    public void setUp() throws Exception {

        dao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao(new ConfigResourceLoader() {
            public Resource getResource(ConfigFile config) {
                return null;
            }
        });

        device = new SimpleDevice(10, 1019); // 1019 = MCT 310
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
    @Test
    public void testGetDefinedAttributes() {

        // Test with supported device type
        Set<Attribute> expectedAttributes = new HashSet<Attribute>();
        expectedAttributes.add(BuiltInAttribute.USAGE);
        expectedAttributes.add(BuiltInAttribute.DEMAND);
        expectedAttributes.add(BuiltInAttribute.LOAD_PROFILE);

        Set<AttributeDefinition> actualAttributes = dao.getDefinedAttributes(device.getDeviceType());

        assertAttributeLookupsMatch("Expected attributes did not match: ", expectedAttributes, actualAttributes);

    }

    @Test
    public void testGetPointTemplateForAttribute() {

        // Test with supported device type
        PointTemplate expectedPointTemplate = new PointTemplate("pulse1",
                                                               PointType.PulseAccumulator,
                                                               2,
                                                               1.0,
                                                               1,
                                                               0,
                                                               3);
        PaoPointTemplate expectedDevicePointTemplate = new PaoPointTemplate(device.getPaoIdentifier(), expectedPointTemplate);

        AttributeDefinition attributeDefinition = dao.getAttributeLookup(device.getDeviceType(), BuiltInAttribute.USAGE);
        PaoPointTemplate actualDevicePointTemplate = attributeDefinition.getPointTemplate(device);
        
        assertEquals("Expected point template did not match: ", expectedDevicePointTemplate, actualDevicePointTemplate);

    }

    /**
     * Test getAllPointTemplates()
     */
    @Test
    public void testGetAllPointTemplates() {

        // Test with supported device type
        Set<PointTemplate> expectedTemplates = this.getExpectedAllTemplates();

        Set<PointTemplate> actualTemplates = dao.getAllPointTemplates(device.getDeviceType());

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates = dao.getAllPointTemplates(dao.getPaoDefinition(device.getDeviceType()));
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
    @Test
    public void testGetInitPointTemplates() {

        // Test with supported device type
        Set<PointTemplate> expectedTemplates = PaoDefinitionDaoImplTest.getExpectedInitTemplates();
        Set<PointTemplate> actualTemplates = dao.getInitPointTemplates(device.getDeviceType());

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates = dao.getInitPointTemplates(dao.getPaoDefinition(device.getDeviceType()));
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
    @Test
    public void testGetDeviceDisplayGroupMap() {

        Multimap<String, PaoDefinition> deviceDisplayGroupMap = dao.getPaoDisplayGroupMap();

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
     * Test getPaoDefinition()
     */
    @Test
    public void testGetPaoDefinition() {

        // Test with supported device type
        PaoDefinition expectedDefinition = new PaoDefinitionImpl(PaoType.getForId(1019),
                                                                       "Device 1",
                                                                       "display1",
                                                                       "MCT310",
                                                                       "change1",
                                                                       true);
        assertEquals("device1 definition is not as expected",
                     expectedDefinition,
                     dao.getPaoDefinition(device.getDeviceType()));

    }

    /**
     * Test getDeviceTypesForChangeGroup()
     */
    public void testGetDevicesForChangeGroup() {

        // Test with supported change group
        Set<PaoDefinition> expectedDeviceTypesList = new HashSet<PaoDefinition>();
        expectedDeviceTypesList.add(new PaoDefinitionImpl(PaoType.getForId(1019),
                                                             "Device 1",
                                                             "display1",
                                                             "MCT310",
                                                             "change1",
                                                             true));
        expectedDeviceTypesList.add(new PaoDefinitionImpl(PaoType.getForId(1022),
                                                             "Device 2",
                                                             "display1",
                                                             "MCT370",
                                                             "change1",
                                                             true));

        PaoDefinitionImpl definition = new PaoDefinitionImpl(PaoType.getForId(1019),
                                                                   "test",
                                                                   "test",
                                                                   "test",
                                                                   "change1",
                                                                   true);
        Set<PaoDefinition> actualDeviceTypesList = dao.getPaosThatPaoCanChangeTo(definition);

        assertEquals("Incorrect device type list for change group: change1",
                     expectedDeviceTypesList,
                     actualDeviceTypesList);

        // Test with invalid change group
        try {
            definition.setChangeGroup("invalid");
            dao.getPaosThatPaoCanChangeTo(definition);
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
    @Test
    public void testGetAffected() {

        Set<PointIdentifier> points = new HashSet<PointIdentifier>();
        Set<CommandDefinition> expectedCommandSet = new HashSet<CommandDefinition>();

        // Define expected points
        PointIdentifier status1 = new PointIdentifier(PointType.Status, 0);
        PointIdentifier pulse1 = new PointIdentifier(PointType.PulseAccumulator, 2);
        PointIdentifier pulse2 = new PointIdentifier(PointType.PulseAccumulator, 4);

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
    
    @Test
    public void testPaoTags() throws Exception {
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.STARS_ACCOUNT_ATTACHABLE_METER));
        assertFalse(dao.isTagSupported(PaoType.MCT370, PaoTag.LOCATE_ROUTE));
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.PORTER_COMMAND_REQUESTS));
        assertFalse(dao.isTagSupported(PaoType.MCT370, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS));
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.MCT_200_SERIES));
        assertFalse(dao.isTagSupported(PaoType.MCT370, PaoTag.MCT_300_SERIES));
    }
    
    @Test
    public void testGetSupportedTags() {
        PaoType mct370 = PaoType.MCT370;
        ImmutableSet<PaoTag> expectedTags = ImmutableSet.of(PaoTag.DLC_ADDRESS_RANGE_ENFORCE,
                                                            PaoTag.DUMMY_LONG_TAG,
                                                            PaoTag.STARS_ACCOUNT_ATTACHABLE_METER,
                                                            PaoTag.PORTER_COMMAND_REQUESTS, 
                                                            PaoTag.MCT_200_SERIES);
        
        PaoDefinition mct370Definition = dao.getPaoDefinition(mct370);
        Set<PaoTag> supportedTags = dao.getSupportedTags(mct370Definition);
        assertEquals(expectedTags, supportedTags);
        
        Set<PaoTag> supportedTags2 = dao.getSupportedTags(mct370);
        assertEquals(expectedTags, supportedTags2);
    }
    
    @Test
    public void testGetPaosThatSupportTag_1() {
        Set<PaoType> expectedTypes = ImmutableSet.of(PaoType.MCT370);
        Set<PaoDefinition> expectedDefinitions = Sets.newHashSet();
        for (PaoType paoType : expectedTypes) {
            PaoDefinition paoDefinition = dao.getPaoDefinition(paoType);
            expectedDefinitions.add(paoDefinition);
        }
        
        Set<PaoDefinition> paosThatSupportTag = dao.getPaosThatSupportTag(PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
        assertEquals(expectedDefinitions, paosThatSupportTag);
        Set<PaoType> paoTypesThatSupportTag = dao.getPaoTypesThatSupportTag(PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
        assertEquals(expectedTypes, paoTypesThatSupportTag);
        
    }
    
    @Test
    public void testGetPaosThatSupportTag_2() {
        Set<PaoType> expectedTypes = ImmutableSet.of(PaoType.MCT370, PaoType.MCT318L);
        Set<PaoDefinition> expectedDefinitions = Sets.newHashSet();
        for (PaoType paoType : expectedTypes) {
            PaoDefinition paoDefinition = dao.getPaoDefinition(paoType);
            expectedDefinitions.add(paoDefinition);
        }
        
        Set<PaoDefinition> paosThatSupportTag = dao.getPaosThatSupportTag(PaoTag.PORTER_COMMAND_REQUESTS);
        assertEquals(expectedDefinitions, paosThatSupportTag);
        Set<PaoType> paoTypesThatSupportTag = dao.getPaoTypesThatSupportTag(PaoTag.PORTER_COMMAND_REQUESTS);
        assertEquals(expectedTypes, paoTypesThatSupportTag);
        
    }
    
    @Test
    public void testGetPaosThatSupportTag_3() {
        Set<PaoType> expectedTypes = ImmutableSet.of(PaoType.MCT370, PaoType.MCT318L);
        Set<PaoDefinition> expectedDefinitions = Sets.newHashSet();
        for (PaoType paoType : expectedTypes) {
            PaoDefinition paoDefinition = dao.getPaoDefinition(paoType);
            expectedDefinitions.add(paoDefinition);
        }
        
        Set<PaoDefinition> paosThatSupportTag = dao.getPaosThatSupportTag(PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS, PaoTag.STARS_ACCOUNT_ATTACHABLE_METER);
        assertEquals(expectedDefinitions, paosThatSupportTag);
        Set<PaoType> paoTypesThatSupportTag = dao.getPaoTypesThatSupportTag(PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS, PaoTag.STARS_ACCOUNT_ATTACHABLE_METER);
        assertEquals(expectedTypes, paoTypesThatSupportTag);
        
    }
    
    @Test
    public void testGetValueForTag() {
        String valueForTagString1 = dao.getValueForTagString(PaoType.MCT370, PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
        assertEquals("1-5", valueForTagString1);
        long valueForTagLong1 = dao.getValueForTagLong(PaoType.MCT370, PaoTag.DUMMY_LONG_TAG);
        assertEquals(8251709l, valueForTagLong1);
        long valueForTagLong2 = dao.getValueForTagLong(PaoType.MCT318L, PaoTag.DUMMY_LONG_TAG);
        assertEquals(0, valueForTagLong2);
        
        // should not work
        try {
            dao.getValueForTagLong(PaoType.MCT370, PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
            fail("should not be compatible get long");
        } catch(Exception e) {}
        
        try {
            dao.getValueForTagString(PaoType.MCT370, PaoTag.DUMMY_LONG_TAG);
            fail("should not be compatible get string");
        } catch(Exception e) {}
        
        try {
            dao.getValueForTagLong(PaoType.MCT310, PaoTag.DUMMY_LONG_TAG);
            fail("should not exist on pao");
        } catch(Exception e) {}
        
        try {
            dao.getValueForTagString(PaoType.MCT370, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS);
            fail("doesn't allow values");
        } catch(Exception e) {}
    }
    
    @Test
    public void testGetAvailableCommands1() {
        PaoDefinition paoDefinition = dao.getPaoDefinition(PaoType.MCT310);
        Set<CommandDefinition> availableCommands = dao.getAvailableCommands(paoDefinition);
        Set<String> availableCommandStrings = Sets.newHashSet();
        for (CommandDefinition command : availableCommands) {
            availableCommandStrings.add(command.getName());
        }
        
        
        assertEquals(ImmutableSet.of("command1", "command2"), availableCommandStrings);
    }
    
    @Test
    public void testGetAvailableCommands2() {
        PaoDefinition paoDefinition = dao.getPaoDefinition(PaoType.MCT318);
        Set<CommandDefinition> availableCommands = dao.getAvailableCommands(paoDefinition);
        assertEquals(ImmutableSet.of(), availableCommands);
    }
    
    /**
     * Test Custom Definition file
     * @throws Exception 
     */
    @Test
    public void testCustomDefinition() throws Exception {
        ClassLoader classLoader = dao.getClass().getClassLoader();

        // Set up the device definition dao with both an inputFile and a customInputFile
        PaoDefinitionDaoImpl dao = new PaoDefinitionDaoImpl();
        URL inputResource = classLoader.getResource("com/cannontech/common/pao/definition/dao/testPaoDefinition.xml");
        dao.setInputFile(new UrlResource(inputResource));
        URL customFileUrl = classLoader.getResource("com/cannontech/common/pao/definition/dao/testCustomPaoDefinition.xml");
        
        URL schemaResource = classLoader.getResource("com/cannontech/common/pao/definition/dao/paoDefinition.xsd");
        dao.setSchemaFile(new UrlResource(schemaResource));

        // setCustomInputFile MUST be called BEFORE initialize
        dao.setCustomInputFile(new UrlResource(customFileUrl));
        dao.setStateDao(new PaoDefinitionDaoImplTest().new MockStateDao());
        dao.setUnitMeasureDao(new PaoDefinitionDaoImplTest().new MockUnitMeasureDao());
        dao.initialize();
        
        
        // Test that the point templates are custom
        
        // Test custom definition overrides standard definition - point
        PointTemplate expectedPulse1PointTemplate = new PointTemplate("pulse1",
		                                                PointType.PulseAccumulator,
		                                                2,
		                                                2.5,
		                                                0,
		                                                0,
		                                                3);
        
        PaoType deviceType = PaoType.getForId(device.getType());
        PointIdentifier pointIdentifier = new PointIdentifier(PointType.PulseAccumulator, 2);
        PointTemplate actualPulse1PointTemplate = dao.getPointTemplateByTypeAndOffset(deviceType, pointIdentifier);
        
        assertEquals("Expected point customizations do not match: ", expectedPulse1PointTemplate, actualPulse1PointTemplate);

        // Test custom definition overrides standard definition - command
        PointIdentifier pulse1 = new PointIdentifier(PointType.PulseAccumulator, 2);
        PointIdentifier pulse2 = new PointIdentifier(PointType.PulseAccumulator, 4);
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
        PaoDefinition expectedDefinition = new PaoDefinitionImpl(PaoType.getForId(1019),
                                                                       "Custom Device 1",
                                                                       "customDisplay1",
                                                                       "MCT310",
                                                                       "customChange1",
                                                                       true);
        assertEquals("device1 definition is not as expected",
                     expectedDefinition,
                     dao.getPaoDefinition(device.getDeviceType()));

    }
    
    @Test
    public void testNoTagsForType() {
        Set<PaoTag> supportedTags = dao.getSupportedTags(PaoType.ALPHA_A1);
        assertNotNull(supportedTags);
    }
    

    /**
     * Helper method to get the set of init point templates for device1
     * @return Set of all point templates
     */
    public static Set<PointTemplate> getExpectedInitTemplates() {
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("pulse1",
                                                PointType.PulseAccumulator,
                                                    2,
                                                    1.0,
                                                    1,
                                                    0,
                                                    3));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("demand1",
                                                PointType.DemandAccumulator,
                                                    1,
                                                    1.0,
                                                    0,
                                                    0,
                                                    3));

        // Analog
        expectedTemplates.add(new PointTemplate("analog1",
                                                    PointType.Analog,
                                                    1,
                                                    1.0,
                                                    1,
                                                    0,
                                                    3));

        return expectedTemplates;

    }

    /**
     * Helper method to get the set of all point templates for device1
     * @return Set of all point templates
     */
    private Set<PointTemplate> getExpectedAllTemplates() {

        // Get the init templates first
        Set<PointTemplate> expectedTemplates = PaoDefinitionDaoImplTest.getExpectedInitTemplates();

        // Add the rest of the templates

        // Pulse Accumulator
        expectedTemplates.add(new PointTemplate("pulse2",
                                                PointType.PulseAccumulator,
                                                    4,
                                                    1.0,
                                                    1,
                                                    0,
                                                    3));

        // Status
        expectedTemplates.add(new PointTemplate("status1", PointType.Status, 1, 1.0, -1, 0,3));

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

        public LiteState findLiteState(int stateGroupID, int rawState) {
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