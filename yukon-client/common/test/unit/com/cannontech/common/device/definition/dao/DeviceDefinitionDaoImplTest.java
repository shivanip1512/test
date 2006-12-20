package com.cannontech.common.device.definition.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.attribute.Attribute;
import com.cannontech.common.device.definition.model.DeviceDisplay;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCT470;

public class DeviceDefinitionDaoImplTest extends TestCase {

    private DeviceDefinitionDao dao = null;
    private DeviceBase device = null;

    protected void setUp() throws Exception {
        dao = new DeviceDefinitionDaoImpl();

        // Use testFixedDeviceAttributes.xml for testing
        ((DeviceDefinitionDaoImpl) dao).setInputFile("com/cannontech/common/device/definition/dao/deviceDefinition.xml");
        ((DeviceDefinitionDaoImpl) dao).initialize();

        // Chose to test a 470 because it has many points both initialized and
        // not
        device = new MCT470();
        device.setDeviceType("MCT-470");
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
        expectedAttributes.add(new Attribute("peakDemand", "peakDemand"));

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

    /**
     * Test getAllPointTemplates()
     */
    public void testGetAllPointTemplates() {

        // Test with supported device type
        // Sort the sets into lists for ease of test trouble shooting
        Set<PointTemplate> expectedTemplates = this.getExpected470AllTemplates();
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
        Set<PointTemplate> expectedTemplates = this.getExpected470InitTemplates();
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

        Map<String, List<DeviceDisplay>> deviceDisplayGroupMap = dao.getDeviceDisplayGroupMap();

        // Make sure there are the correct number of device type groups
        assertEquals("There should be 5 device type groups", 5, deviceDisplayGroupMap.keySet()
                                                                                     .size());

        // Make sure there are the correct number of mct device types
        try {
            assertEquals("There should be 23 MCT device types",
                         23,
                         deviceDisplayGroupMap.get("MCT").size());
        } catch (NullPointerException e) {
            fail("There is not an MCT device type group");
        }

        // Make sure there are the correct number of Signal Transmitters device
        // types
        try {
            assertEquals("There should be 17 Signal Transmitters device types",
                         17,
                         deviceDisplayGroupMap.get("Signal Transmitters").size());
        } catch (NullPointerException e) {
            fail("There is not an Signal Transmitters device type group");
        }

        // Make sure there are the correct number of Electronic Meters device
        // types
        try {
            assertEquals("There should be 16 Electronic Meters device types",
                         16,
                         deviceDisplayGroupMap.get("Electronic Meters").size());
        } catch (NullPointerException e) {
            fail("There is not an Electronc Meters device type group");
        }

        // Make sure there are the correct number of RTU device
        // types
        try {
            assertEquals("There should be 7 RTU device types", 7, deviceDisplayGroupMap.get("RTU")
                                                                                       .size());
        } catch (NullPointerException e) {
            fail("There is not an RTU device type group");
        }

        // Make sure there are the correct number of Virtual device
        // types
        try {
            assertEquals("There should be 1 Virtual device types",
                         1,
                         deviceDisplayGroupMap.get("Virtual").size());
        } catch (NullPointerException e) {
            fail("There is not an Virtual device type group");
        }

    }

    /**
     * Helper method to get the set of init point templates for the 470
     * @return Set of all point templates
     */
    private Set<PointTemplate> getExpected470InitTemplates() {
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("kWh", 2, 1, 1.0, 1, 0, true, Attribute.USAGE));
        expectedTemplates.add(new PointTemplate("Blink Count", 2, 20, 1.0, 9, 0, true, null));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("kW", 3, 1, 1.0, 0, 0, true, Attribute.DEMAND));
        expectedTemplates.add(new PointTemplate("kW-LP", 3, 101, 1.0, 0, 0, true, null));

        // Analog
        expectedTemplates.add(new PointTemplate("Total kWh",
                                                1,
                                                1,
                                                1.0,
                                                1,
                                                0,
                                                true,
                                                new Attribute("totalUsage", "totalUsage")));
        expectedTemplates.add(new PointTemplate("Peak kW (Rate A kW)",
                                                1,
                                                2,
                                                1.0,
                                                0,
                                                0,
                                                true,
                                                new Attribute("peakDemand", "peakDemand")));
        expectedTemplates.add(new PointTemplate("Rate A kWh", 1, 3, 1.0, 1, 0, true, null));
        expectedTemplates.add(new PointTemplate("Last Interval kW", 1, 10, 1.0, 0, 0, true, null));
        expectedTemplates.add(new PointTemplate("Outages", 1, 100, 1.0, 31, 0, true, null));

        return expectedTemplates;

    }

    /**
     * Helper method to get the set of all point templates for the 470
     * @return Set of all point templates
     */
    private Set<PointTemplate> getExpected470AllTemplates() {

        // Get the init templates first
        Set<PointTemplate> expectedTemplates = this.getExpected470InitTemplates();

        // Add the rest of the 470 templates

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("Channel 2 kWh", 2, 2, 0.01, 1, 0, false, null));
        expectedTemplates.add(new PointTemplate("Channel 3 kWh", 2, 3, 0.01, 1, 0, false, null));
        expectedTemplates.add(new PointTemplate("Channel 4 kWh", 2, 4, 0.01, 1, 0, false, null));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("Channel 2 kW", 3, 2, 0.01, 0, 0, false, null));
        expectedTemplates.add(new PointTemplate("Channel 3 kW", 3, 3, 0.01, 0, 0, false, null));
        expectedTemplates.add(new PointTemplate("Channel 4 kW", 3, 4, 0.01, 0, 0, false, null));

        // Analog
        expectedTemplates.add(new PointTemplate("Rate B kW", 1, 4, 1.0, 0, 0, false, null));
        expectedTemplates.add(new PointTemplate("Rate B kWh", 1, 5, 1.0, 1, 0, false, null));
        expectedTemplates.add(new PointTemplate("Rate C kW", 1, 6, 1.0, 0, 0, false, null));
        expectedTemplates.add(new PointTemplate("Rate C kWh", 1, 7, 1.0, 1, 0, false, null));
        expectedTemplates.add(new PointTemplate("Rate D kW", 1, 8, 1.0, 0, 0, false, null));
        expectedTemplates.add(new PointTemplate("Rate D kWh", 1, 9, 1.0, 1, 0, false, null));

        // Status
        expectedTemplates.add(new PointTemplate("Disconnect Status", 0, 1, 1.0, -1, -6, false, null));
        expectedTemplates.add(new PointTemplate("Communication Status (CVD)",
                                                0,
                                                2000,
                                                1.0,
                                                -1,
                                                0,
                                                false,
                                                null));

        return expectedTemplates;
    }
}
