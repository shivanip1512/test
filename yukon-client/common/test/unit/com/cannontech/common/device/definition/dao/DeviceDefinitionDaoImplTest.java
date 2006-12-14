package com.cannontech.common.device.definition.dao;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.attribute.Attribute;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDaoImpl;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCT410CL;

public class DeviceDefinitionDaoImplTest extends TestCase {

    private DeviceDefinitionDaoImpl dao = null;
    private DeviceBase device = null;

    protected void setUp() throws Exception {
        dao = new DeviceDefinitionDaoImpl();

        // Use testFixedDeviceAttributes.xml for testing
        dao.setInputFile("com/cannontech/common/device/definition/dao/testDeviceDefinition.xml");
        dao.initialize();

        device = new MCT410CL();
        device.setDeviceType("MCT410CL");
    }

    /**
     * Test getAvailableAttributes()
     */
    public void testGetAvailableAttributes() {

        // Test with supported device type
        Set<Attribute> expectedAttributes = new HashSet<Attribute>();
        expectedAttributes.add(Attribute.USAGE);
        expectedAttributes.add(Attribute.DEMAND);
        expectedAttributes.add(new Attribute("status", "status"));
        expectedAttributes.add(new Attribute("analog1", "analog1"));
        expectedAttributes.add(new Attribute("rateAUsage", "rateAUsage"));
        expectedAttributes.add(new Attribute("rateADemand", "rateADemand"));

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
        Set<PointTemplate> expectedTemplates = this.getExpectedAllTemplates();
        Set<PointTemplate> actualTemplates = dao.getAllPointTemplates(device);

        assertEquals("Expected point templates did not match: ", expectedTemplates, actualTemplates);

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
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();
        expectedTemplates.add(new PointTemplate("Total kWh", 2, 1, 1.0, 1, 0, true, Attribute.USAGE));

        Set<PointTemplate> actualTemplates = dao.getInitPointTemplates(device);

        assertEquals("Expected point templates did not match: ", expectedTemplates, actualTemplates);

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
     * Helper method to get the set of all point templates
     * @return Set of all point templates
     */
    private Set<PointTemplate> getExpectedAllTemplates() {

        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        expectedTemplates.add(new PointTemplate("Total kWh", 2, 1, 1.0, 1, 0, true, Attribute.USAGE));
        expectedTemplates.add(new PointTemplate("Rate A kWh",
                                                2,
                                                2,
                                                1.0,
                                                1,
                                                0,
                                                false,
                                                new Attribute("rateAUsage", "rateAUsage")));
        expectedTemplates.add(new PointTemplate("Total kW",
                                                3,
                                                11,
                                                1.0,
                                                0,
                                                0,
                                                false,
                                                Attribute.DEMAND));
        expectedTemplates.add(new PointTemplate("Rate A kW",
                                                3,
                                                12,
                                                1.0,
                                                0,
                                                0,
                                                false,
                                                new Attribute("rateADemand", "rateADemand")));
        expectedTemplates.add(new PointTemplate("Status A",
                                                0,
                                                123,
                                                1.0,
                                                -1,
                                                -6,
                                                false,
                                                new Attribute("status", "status")));
        expectedTemplates.add(new PointTemplate("Analog 2", 1, 2, 1.0, 0, 0, false, null));
        expectedTemplates.add(new PointTemplate("Analog 1",
                                                1,
                                                1,
                                                1.0,
                                                1,
                                                0,
                                                false,
                                                new Attribute("analog1", "analog1")));

        return expectedTemplates;
    }
}
