package com.cannontech.common.pao.definition.dao;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.loader.DefinitionLoaderServiceImpl;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinitionImpl;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.impl.PointCreationServiceImpl;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class PaoDefinitionDaoImplTest {
    private PaoDefinitionDao dao;
    private SimpleDevice device;

    public static PaoDefinitionDao getTestPaoDefinitionDao() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext();
        PaoDefinitionDaoImpl paoDefinitionDaoImpl = new PaoDefinitionDaoImpl();
        DefinitionLoaderServiceImpl definitionLoaderService = new DefinitionLoaderServiceImpl();
        Resource paoXsd = ctx.getResource("classpath:pao/definition/pao.xsd");
        Resource pointsXsd = ctx.getResource("classpath:pao/definition/points.xsd");
        Resource overrideXsd = ctx.getResource("classpath:pao/definition/override.xsd");

        StateGroupDao stateGroupDao = createNiceMock(StateGroupDao.class);
        stateGroupDao.getStateGroup(anyObject(String.class));
        expectLastCall().andAnswer(() -> {
            ArrayList<LiteState> states = new ArrayList<>();
            states.add(new LiteState(0, "Decommissioned", 0, 0, 0));
            return new LiteStateGroup(0, "state0", states);
        }).anyTimes();

        stateGroupDao.getStateGroup(anyInt());
        expectLastCall().andAnswer(() -> new LiteStateGroup(0, "state0")).anyTimes();

        PointDao pointDao = new MockPointDao();
        PointCreationServiceImpl pointCreationServiceImpl = new PointCreationServiceImpl();
        ReflectionTestUtils.setField(pointDao, "pointCreationService", pointCreationServiceImpl);
        ReflectionTestUtils.setField(definitionLoaderService, "paoXsd", paoXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "pointsXsd", pointsXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "overrideXsd", overrideXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "stateGroupDao", stateGroupDao);
        replay(stateGroupDao);
        ReflectionTestUtils.setField(definitionLoaderService, "pointDao", pointDao);
        ReflectionTestUtils.setField(paoDefinitionDaoImpl, "definitionLoaderService", definitionLoaderService);
        definitionLoaderService.load();
        paoDefinitionDaoImpl.initialize();
        return paoDefinitionDaoImpl;
    }

    @Before
    public void setup() {
        dao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
        device = new SimpleDevice(10, PaoType.MCT310.getDeviceTypeId());
    }

    @Test
    public void test_getDefinedAttributes() {
        // Test with supported device type
        Set<Attribute> expectedAttributes = new HashSet<>();
        expectedAttributes.add(BuiltInAttribute.USAGE);
        expectedAttributes.add(BuiltInAttribute.BLINK_COUNT);
        expectedAttributes.add(BuiltInAttribute.OUTAGE_STATUS);
        expectedAttributes.add(BuiltInAttribute.COMM_STATUS);
        expectedAttributes.add(BuiltInAttribute.POWER_FAIL_FLAG);

        Set<AttributeDefinition> actualAttributes = dao.getDefinedAttributes(device.getDeviceType());
        assertAttributeLookupsMatch("Expected attributes did not match: ", expectedAttributes, actualAttributes);
    }

    @Test
    public void test_getPointTemplateForAttribute() {
        // Test with supported device type
        PointTemplate expectedPointTemplate = new PointTemplate("kWh", PointType.PulseAccumulator, 1, 0.01, 1, 0, 1);
        PaoPointTemplate expectedDevicePointTemplate =
            new PaoPointTemplate(device.getPaoIdentifier(), expectedPointTemplate);

        AttributeDefinition attributeDefinition =
            dao.getAttributeLookup(device.getDeviceType(), BuiltInAttribute.USAGE);
        PaoPointTemplate actualDevicePointTemplate = attributeDefinition.getPointTemplate(device);
        assertEquals("Expected point template did not match: ", expectedDevicePointTemplate, actualDevicePointTemplate);
    }

    @Test
    public void test_GetAllPointTemplates() {
        // Test with supported device type
        Set<PointTemplate> expectedTemplates = getExpectedAllTemplates();

        Set<PointTemplate> actualTemplates = dao.getAllPointTemplates(device.getDeviceType());

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates =
            dao.getAllPointTemplates(dao.getPaoDefinition(device.getDeviceType()));
        assertEquals("Expected definition templates did not match device templates", getSortedList(actualTemplates),
            getSortedList(acutalDefinitionTemplates));

        assertEquals("Expected all point templates did not match: ", getSortedList(expectedTemplates),
            getSortedList(actualTemplates));
    }

    @Test
    public void test_getInitPointTemplates() {
        // Test with supported device type
        Set<PointTemplate> expectedTemplates = PaoDefinitionDaoImplTest.getExpectedInitTemplates();
        Set<PointTemplate> actualTemplates = dao.getInitPointTemplates(device.getDeviceType());

        // Test the overloaded method - should return same results
        Set<PointTemplate> acutalDefinitionTemplates =
            dao.getInitPointTemplates(dao.getPaoDefinition(device.getDeviceType()));
        assertEquals("Expected definition templates did not match device templates", getSortedList(actualTemplates),
            getSortedList(acutalDefinitionTemplates));

        assertEquals("Expected init point templates did not match: ", getSortedList(expectedTemplates),
            getSortedList(actualTemplates));

    }

    @Test
    public void test_getDeviceDisplayGroupMap() {

        Multimap<String, PaoDefinition> deviceDisplayGroupMap = dao.getPaoDisplayGroupMap();

        // Make sure there are the correct number of device type groups
        assertEquals("There should be 15 device type groups", 15, deviceDisplayGroupMap.keySet().size());

        // Make sure there are the correct number of IPC device types
        try {
            assertEquals("There should be 4 IPC device types", 4, deviceDisplayGroupMap.get("IPC").size());
        } catch (NullPointerException e) {
            fail("There is not a display1 device type group");
        }

        // Make sure there are the correct number of RTU device types
        try {
            assertEquals("There should be 7 RTU device types", 7, deviceDisplayGroupMap.get("RTU").size());
        } catch (NullPointerException e) {
            fail("There is not a display2 device type group");
        }

    }

    @Test
    public void test_getPaoDefinition() {

        // Test with supported device type
        PaoDefinition expectedDefinition =
            new PaoDefinitionImpl(PaoType.getForId(1019), "MCT-310", "MCT", "meter", false);
        assertEquals("device1 definition is not as expected", expectedDefinition,
            dao.getPaoDefinition(device.getDeviceType()));

    }

    @Test
    public void test_getPaosThatPaoCanChangeTo_forSupportedChangeGroup() {
        Set<PaoDefinition> expectedDeviceTypesList = new HashSet<>();
        expectedDeviceTypesList.add(new PaoDefinitionImpl(PaoType.MCT250, "MCT-250", "MCT", "meter", false));
        expectedDeviceTypesList.add(new PaoDefinitionImpl(PaoType.RFN410FX, "RFN-410fX", "RFMESH", "meter", true));

        PaoDefinitionImpl definition = new PaoDefinitionImpl(PaoType.MCT310, "MCT-310", "MCT", "meter", false);
        Set<PaoDefinition> actualDeviceTypesList = dao.getPaosThatPaoCanChangeTo(definition);
        for (PaoDefinition paoDefinition : expectedDeviceTypesList) {
            assertTrue("Device Type list for change group : meter contains",
                actualDeviceTypesList.contains(paoDefinition));

        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getPaosThatPaoCanChangeTo_invalidChangeGroup() {
        PaoDefinitionImpl definition = new PaoDefinitionImpl(PaoType.MCT310, "MCT-310", "MCT", "meter", false);
        definition.setChangeGroup("invalid");
        dao.getPaosThatPaoCanChangeTo(definition);
    }

    @Test
    public void test_getCommandsThatAffectPoints() {
        Set<PointIdentifier> points = new HashSet<>();
        Set<CommandDefinition> expectedCommandSet = new HashSet<>();

        // Define expected points
        PointIdentifier status1 = new PointIdentifier(PointType.Status, 0);
        PointIdentifier pulse1 = new PointIdentifier(PointType.PulseAccumulator, 1);
        PointIdentifier pulse2 = new PointIdentifier(PointType.PulseAccumulator, 20);

        // Define expected command definitions
        CommandDefinition command1 = new CommandDefinition("Read Usage");
        command1.addCommandString("getvalue kWh");
        command1.addAffectedPoint(pulse1);

        CommandDefinition command2 = new CommandDefinition("Read Blink Count");
        command2.addCommandString("getvalue powerfail");
        command2.addAffectedPoint(pulse2);

        // Test with no expected commands
        points.add(status1);
        Set<CommandDefinition> actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected no commands", expectedCommandSet, actualCommands);

        // Test with one expected command - Read Blink Count
        points.clear();
        points.add(pulse2);
        expectedCommandSet.add(command2);

        actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected 1 command", expectedCommandSet, actualCommands);

        // Test with one expected command - Read Usage
        points.clear();
        points.add(pulse1);
        expectedCommandSet.clear();
        expectedCommandSet.add(command1);

        actualCommands = dao.getCommandsThatAffectPoints(device.getDeviceType(), points);
        assertEquals("Expected 1 command", expectedCommandSet, actualCommands);

    }

    @Test
    public void test_paoTags() throws Exception {
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.STARS_ACCOUNT_ATTACHABLE_METER));
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.LOCATE_ROUTE));
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.PORTER_COMMAND_REQUESTS));
        assertFalse(dao.isTagSupported(PaoType.MCT370, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS));
        assertTrue(dao.isTagSupported(PaoType.MCT370, PaoTag.MCT_300_SERIES));
        assertFalse(dao.isTagSupported(PaoType.MCT370, PaoTag.MCT_200_SERIES));
    }

    @Test
    public void test_getSupportedTags() {
        ImmutableSet<PaoTag> expectedTags =
            ImmutableSet.of(PaoTag.COMMANDER_REQUESTS, PaoTag.DEVICE_ICON_TYPE, PaoTag.DLC_ADDRESS_RANGE_ENFORCE,
                PaoTag.LOCATE_ROUTE, PaoTag.MCT_300_SERIES, PaoTag.METER_DETAIL_DISPLAYABLE, PaoTag.OUTAGE,
                PaoTag.PORTER_COMMAND_REQUESTS, PaoTag.STARS_ACCOUNT_ATTACHABLE_METER, PaoTag.USES_METER_NUMBER_FOR_MSP);

        PaoDefinition mct370Definition = dao.getPaoDefinition(PaoType.MCT370);
        Set<PaoTag> supportedTags = dao.getSupportedTags(mct370Definition);
        assertEquals(expectedTags, supportedTags);

        Set<PaoTag> supportedTags2 = dao.getSupportedTags(PaoType.MCT370);
        assertEquals(expectedTags, supportedTags2);
    }

    @Test
    public void test_getPaosThatSupportTag_1() {
        Set<PaoType> expectedTypes = ImmutableSet.of(PaoType.LM_HONEYWELL_PROGRAM);
        Set<PaoDefinition> expectedDefinitions = Sets.newHashSet();
        for (PaoType paoType : expectedTypes) {
            PaoDefinition paoDefinition = dao.getPaoDefinition(paoType);
            expectedDefinitions.add(paoDefinition);
        }

        Set<PaoDefinition> paosThatSupportTag = dao.getPaosThatSupportTag(PaoTag.HONEYWELL_PROGRAM_ENROLLMENT);//
        assertEquals(expectedDefinitions, paosThatSupportTag);
        Set<PaoType> paoTypesThatSupportTag = dao.getPaoTypesThatSupportTag(PaoTag.HONEYWELL_PROGRAM_ENROLLMENT);
        assertEquals(expectedTypes, paoTypesThatSupportTag);

    }

    @Test
    public void test_getPaosThatSupportTag_2() {
        Set<PaoType> expectedTypes = ImmutableSet.of(PaoType.LM_HONEYWELL_PROGRAM, PaoType.LM_ECOBEE_PROGRAM);
        Set<PaoDefinition> expectedDefinitions = Sets.newHashSet();
        for (PaoType paoType : expectedTypes) {
            PaoDefinition paoDefinition = dao.getPaoDefinition(paoType);
            expectedDefinitions.add(paoDefinition);
        }

        Set<PaoDefinition> paosThatSupportTag =
            dao.getPaosThatSupportTag(PaoTag.HONEYWELL_PROGRAM_ENROLLMENT, PaoTag.ECOBEE_PROGRAM_ENROLLMENT);
        assertEquals(expectedDefinitions, paosThatSupportTag);
        Set<PaoType> paoTypesThatSupportTag =
            dao.getPaoTypesThatSupportTag(PaoTag.HONEYWELL_PROGRAM_ENROLLMENT, PaoTag.ECOBEE_PROGRAM_ENROLLMENT);
        assertEquals(expectedTypes, paoTypesThatSupportTag);

    }

    @Test
    public void test_getValueForTag() {
        String valueForTagString1 = dao.getValueForTagString(PaoType.MCT370, PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
        assertEquals("0-4194303", valueForTagString1);
        // should not work
        try {
            dao.getValueForTagString(PaoType.MCT370, PaoTag.DUMMY_LONG_TAG);
            fail("should not be compatible get string");
        } catch (Exception e) {
            // exception expected for test
        }

        try {
            dao.getValueForTagString(PaoType.MCT370, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS);
            fail("doesn't allow values");
        } catch (Exception e) {
            // exception expected for test
        }
    }

    @Test
    public void test_getAvailableCommands1() {
        PaoDefinition paoDefinition = dao.getPaoDefinition(PaoType.MCT310);
        Set<CommandDefinition> availableCommands = dao.getAvailableCommands(paoDefinition);
        Set<String> availableCommandStrings = Sets.newHashSet();
        for (CommandDefinition command : availableCommands) {
            availableCommandStrings.add(command.getName());
        }
        assertEquals(ImmutableSet.of("Read Blink Count", "Read Usage"), availableCommandStrings);
    }

    @Test
    public void test_getAvailableCommands2() {
        PaoDefinition paoDefinition = dao.getPaoDefinition(PaoType.TCU5000);
        Set<CommandDefinition> availableCommands = dao.getAvailableCommands(paoDefinition);
        assertEquals(ImmutableSet.of(), availableCommands);
    }

    @Test
    public void test_noTagsForType() {
        Set<PaoTag> supportedTags = dao.getSupportedTags(PaoType.ALPHA_A1);
        assertNotNull(supportedTags);
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
     * Helper method to get the set of all point templates for device
     * 
     * @return Set of all point templates
     */
    private static Set<PointTemplate> getExpectedAllTemplates() {
        // Get the init templates first
        Set<PointTemplate> expectedTemplates = PaoDefinitionDaoImplTest.getExpectedInitTemplates();

        // Add the rest of the templates
        // Status
        expectedTemplates.add(new PointTemplate("Comm Status", PointType.Status, 2000, 1.0, -1, 0, 3));
        expectedTemplates.add(new PointTemplate("Short Power Fail Flag", PointType.Status, 11, 1.0, -1, 0, 3));
        expectedTemplates.add(new PointTemplate("Over Flow Flag", PointType.Status, 12, 1.0, -1, 0, 3));
        return expectedTemplates;
    }

    /**
     * Helper method to get the set of init point templates for device
     * 
     * @return Set of all point templates
     */
    public static Set<PointTemplate> getExpectedInitTemplates() {
        Set<PointTemplate> expectedTemplates = new HashSet<>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("kWh", PointType.PulseAccumulator, 1, 0.01, 1, 0, 1));

        expectedTemplates.add(new PointTemplate("Blink Count", PointType.PulseAccumulator, 20, 1.0, 9, 0, 3));

        // Status
        expectedTemplates.add(new PointTemplate("Power Fail", PointType.Status, 10, 1.0, -1, 0, 3));
        PointTemplate outageStatusTemplate = new PointTemplate("Outage Status", PointType.Status, 1000, 1.0, -1, 0, 3);
        outageStatusTemplate.setPointArchiveType(PointArchiveType.ON_CHANGE);
        expectedTemplates.add(outageStatusTemplate);
        return expectedTemplates;

    }

    /**
     * Helper method to turn a set into a sorted list - This method should be
     * used to more easily see the differences between to sets if a test fails
     * 
     * @param set - Set to get list for
     * @return A sorted list containing ever object that was in the set
     */
    private static List<PointTemplate> getSortedList(Set<PointTemplate> set) {
        return set.stream().sorted().collect(Collectors.toList());
    }

}
