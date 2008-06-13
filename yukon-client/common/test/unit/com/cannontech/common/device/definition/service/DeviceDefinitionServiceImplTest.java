package com.cannontech.common.device.definition.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.service.AttributeServiceImpl;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDaoImplTest;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointServiceImpl;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * Test class for DeviceDefinitionService
 */
public class DeviceDefinitionServiceImplTest {

    private SimpleDeviceDefinitionServiceImpl service = null;
    private PointServiceImpl pointService = null;
    private YukonDevice device = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private AttributeServiceImpl attributeService = null;
    private PointDao pointDao;
    
    @Before
    public void setUp() throws Exception {

        service = new SimpleDeviceDefinitionServiceImpl();
        deviceDefinitionDao = DeviceDefinitionDaoImplTest.getTestDeviceDefinitionDao();
        service.setDeviceDefinitionDao(deviceDefinitionDao);

        pointDao = new MockPointDao();
        
        pointService = new PointServiceImpl();
        // Create the point service for testing
        pointService.setPointDao(pointDao);
        pointService.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        service.setPointService(pointService);
        service.setPointDao(pointDao);

        // Create the attribute service for testing
        attributeService = new AttributeServiceImpl();
        attributeService.setDeviceDefinitionDao(deviceDefinitionDao);
        attributeService.setPointService(pointService);
        attributeService.setPointDao(pointDao);

        device = new YukonDevice(1, DeviceTypes.MCT310);

    }

    /**
     * Test createDefaultPointsForDevice()
     */
    @Test
    public void testCreateDefaultPointsForDevice() {

        // Test with supported device
        List<PointBase> expectedPoints = new ArrayList<PointBase>();
        expectedPoints.add(pointService.createPoint(2, "pulse1", 1, 1, 1.0, 1, 0));
        expectedPoints.add(pointService.createPoint(3, "demand1", 1, 1, 1.0, 0, 0));
        expectedPoints.add(pointService.createPoint(1, "analog1", 1, 1, 1.0, 1, 0));

        List<PointBase> actualPoints = service.createDefaultPointsForDevice(device);

        assertEquals("Default points weren't as expected", expectedPoints, actualPoints);

        // Test with unsupported device
        try {
            device.setType(9999999);
            actualPoints = service.createDefaultPointsForDevice(device);
            fail("createDefaultPointsForDevice should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test isDeviceTypeChangeable()
     */
    @Test
    public void testIsDeviceTypeChangeable() {

        // Test with changeable device
        assertTrue("device1 is changeable", service.isDeviceTypeChangeable(device));

        // Test with device that is not changeable
        device.setType(DeviceTypes.MCT318L);
        assertTrue("device3 is not changeable", !service.isDeviceTypeChangeable(device));

        // Test with unsupported device
        try {
            device.setType(999999);
            service.isDeviceTypeChangeable(device);
            fail("isDeviceTypeChangeable should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test getChangeableDevices()
     */
    @Test
    public void testGetChangeableDevices() {

        // Test with changeable device
        Set<DeviceDefinition> expectedDevices = new HashSet<DeviceDefinition>();
        YukonDevice device2 = new YukonDevice(11, DeviceTypes.MCT370);
        expectedDevices.add(deviceDefinitionDao.getDeviceDefinition(device2));

        Set<DeviceDefinition> actualDevices = service.getChangeableDevices(device);

        assertEquals("Changeable devices were not as expected", expectedDevices, actualDevices);

        // Test with device that is not changeable
        device.setType(DeviceTypes.MCT318L);
        Set<DeviceDefinition> actualDevices2 = service.getChangeableDevices(device);
        assertTrue("Should be empty set", actualDevices2.isEmpty());

        // Test with unsupported device
        try {
            device.setType(999999);
            service.getChangeableDevices(device);
            fail("getChangeableDevices should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test createAllPointsForDevice()
     */
    @Test
    public void testCreateAllPointsForDevice() {

        // Test with supported device
        List<PointBase> expectedPoints = new ArrayList<PointBase>();
        expectedPoints.add(pointService.createPoint(0, "status1", 1, 1, 1.0, 0, 0));
        expectedPoints.add(pointService.createPoint(2, "pulse1", 1, 1, 1.0, 1, 0));
        expectedPoints.add(pointService.createPoint(2, "pulse2", 1, 2, 0.1, 1, 0));
        expectedPoints.add(pointService.createPoint(3, "demand1", 1, 1, 1.0, 0, 0));
        expectedPoints.add(pointService.createPoint(1, "analog1", 1, 1, 1.0, 1, 0));

        List<PointBase> actualPoints = service.createAllPointsForDevice(device);

        assertEquals("All points weren't as expected", expectedPoints, actualPoints);

        // Test with unsupported device
        try {
            device.setType(999999);
            actualPoints = service.createAllPointsForDevice(device);
            fail("createAllPointsForDevice should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test getPointTemplatesToAdd()
     */
    @Test
    public void testGetPointTemplatesToAdd() {

        // Test add points from type 'device2' to type 'device1'
        device.setType(DeviceTypes.MCT370);
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("pulse1",
                                                    2,
                                                    2,
                                                    1.0,
                                                    1,
                                                    0,
                                                    true));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("demand1",
                                                    3,
                                                    1,
                                                    1.0,
                                                    0,
                                                    0,
                                                    true));

        
        Set<PointTemplate> actualTemplates = service.getPointTemplatesToAdd(device,
                                                                            new DeviceDefinitionImpl(1019,
                                                                                                     "Device1",
                                                                                                     "display1",
                                                                                                     "MCT310",
                                                                                                     "change1"));

        assertEquals("Point templates to add were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test add points from type 'device1' to type 'device3' (is an invalid
        // change)
        try {
            device.setType(DeviceTypes.MCT318L);
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

            device.setType(DeviceTypes.MCT310);
            service.getPointTemplatesToAdd(device, deviceDefinition);
            fail("getPointTemplatesToAdd should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }
    }

    /**
     * Test getPointTemplatesToRemove()
     */
    @Test
    public void testGetPointTemplatesToRemove() {

        // Test remove points from type 'device1' to type 'device2'
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Pulse Accumulators
        expectedTemplates.add(new PointTemplate("pulse1",
                                                    2,
                                                    2,
                                                    1.0,
                                                    1,
                                                    0,
                                                    true));

        expectedTemplates.add(new PointTemplate("pulse2",
									                2,
									                4,
									                1.0,
									                1,
									                0,
									                true));

        // Demand Accumulators
        expectedTemplates.add(new PointTemplate("demand1",
                                                    3,
                                                    1,
                                                    1.0,
                                                    0,
                                                    0,
                                                    true));

        Set<PointTemplate> actualTemplates = service.getPointTemplatesToRemove(device,
                                                                               new DeviceDefinitionImpl(1022,
                                                                                                        "Device2",
                                                                                                        "display2",
                                                                                                        "MCT370",
                                                                                                        "change1"));

        assertEquals("Point templates to remove were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test remove points from type 'device1' to type 'device3' (is an
        // invalid change)
        try {
            device.setType(DeviceTypes.MCT318L);
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

            device.setType(DeviceTypes.MCT310);
            service.getPointTemplatesToRemove(device, deviceDefinition);
            fail("getPointTemplatesToRemove should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test getPointTemplatesToTransfer()
     */
    @Test
    public void testGetPointTemplatesToTransfer() {

        // Test remove points from type 'device1' to type 'device2'
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();

        // Analog
        expectedTemplates.add(new PointTemplate("analog1",
                                                    1,
                                                    1,
                                                    1.0,
                                                    1,
                                                    0,
                                                    true));

        Set<PointTemplate> actualTemplates = service.getPointTemplatesToTransfer(device,
                                                                                 new DeviceDefinitionImpl(1022,
                                                                                                          "Device2",
                                                                                                          "display2",
                                                                                                          "MCT370",
                                                                                                          "change1"));

        assertEquals("Point templates to transfer were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test transfer points from type 'device1' to type 'device3' (is an
        // invalid change)
        try {
            device.setType(DeviceTypes.MCT318L);
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

            device.setType(DeviceTypes.MCT310);
            service.getPointTemplatesToTransfer(device, deviceDefinition);
            fail("getPointTemplatesToTransfer should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test getNewPointTemplatesForTransfer()
     */
    @Test
    public void testGetNewPointTemplatesForTransfer() {

        // Test remove points from type 'device1' to type 'device2'
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();
        expectedTemplates.add(new PointTemplate("analog1",
											        1,
											        1,
											        1.0,
											        1,
											        0,
											        false));

        Set<PointTemplate> actualTemplates = service.getNewPointTemplatesForTransfer(device,
                                                                                     new DeviceDefinitionImpl(DeviceTypes.MCT370,
                                                                                                              "Device2",
                                                                                                              "display2",
                                                                                                              "MCT370",
                                                                                                              "change1"));

        assertEquals("New point templates to transfer were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test transfer points from type 'device1' to type 'device3' (is an
        // invalid change)
        try {
            device.setType(DeviceTypes.MCT318L);
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

            device.setType(DeviceTypes.MCT310);
            service.getNewPointTemplatesForTransfer(device, deviceDefinition);
            fail("getPointTemplatesToTransfer should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }
}
