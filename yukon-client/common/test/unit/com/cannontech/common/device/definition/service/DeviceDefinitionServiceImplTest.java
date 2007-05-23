package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.attribute.model.UserDefinedAttribute;
import com.cannontech.common.device.attribute.service.AttributeServiceImpl;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDaoImplTest;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.PointTemplateImpl;
import com.cannontech.common.device.service.PointServiceImpl;
import com.cannontech.common.mock.MockDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * Test class for DeviceDefinitionService
 */
public class DeviceDefinitionServiceImplTest extends TestCase {

    private DeviceDefinitionServiceImpl service = null;
    private PointServiceImpl pointService = null;
    private DeviceBase device = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private AttributeServiceImpl attributeService = null;

    protected void setUp() throws Exception {

        service = new DeviceDefinitionServiceImpl();
        deviceDefinitionDao = DeviceDefinitionDaoImplTest.getTestDeviceDefinitionDao();
        service.setDeviceDefinitionDao(deviceDefinitionDao);

        pointService = new PointServiceImpl();
        // Create the point service for testing
        pointService.setPointDao(new MockPointDao());
        pointService.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        service.setPointService(pointService);

        // Create the attribute service for testing
        attributeService = new AttributeServiceImpl();
        attributeService.setDeviceDefinitionDao(deviceDefinitionDao);
        attributeService.setPointService(pointService);
        service.setAttributeService(attributeService);

        device = new MockDevice();
        device.setDeviceType("MCT-310");
        device.setPAOName("Test Device");
        device.setDeviceID(1);
        device.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);

    }

    /**
     * Test createDefaultPointsForDevice()
     */
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
            device.setDeviceType("invalid");
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
    public void testIsDeviceTypeChangeable() {

        // Test with changeable device
        assertTrue("device1 is changeable", service.isDeviceTypeChangeable(device));

        // Test with device that is not changeable
        device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT318L));
        assertTrue("device3 is not changeable", !service.isDeviceTypeChangeable(device));

        // Test with unsupported device
        try {
            device.setDeviceType("invalid");
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
    public void testGetChangeableDevices() {

        // Test with changeable device
        Set<DeviceDefinition> expectedDevices = new HashSet<DeviceDefinition>();
        MockDevice device2 = new MockDevice();
        device2.setDeviceID(11);
        device2.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
        device2.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT370));
        expectedDevices.add(deviceDefinitionDao.getDeviceDefinition(getLiteForDevice(device2)));

        Set<DeviceDefinition> actualDevices = service.getChangeableDevices(device);

        assertEquals("Changeable devices were not as expected", expectedDevices, actualDevices);

        // Test with device that is not changeable
        try {
            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT318L));
            service.getChangeableDevices(device);
            fail("getChangeableDevices should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

        // Test with unsupported device
        try {
            device.setDeviceType("invalid");
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
            device.setDeviceType("invalid");
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
    public void testGetPointTemplatesToAdd() {

        // Test add points from type 'device2' to type 'device1'
        device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT370));
        Set<PointTemplate> expectedTemplates = DeviceDefinitionDaoImplTest.getExpectedInitTemplates();

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
            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT318L));
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(getLiteForDevice(device));

            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT310));
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
    public void testGetPointTemplatesToRemove() {

        // Test remove points from type 'device1' to type 'device2'
        Set<PointTemplate> expectedTemplates = DeviceDefinitionDaoImplTest.getExpectedInitTemplates();

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
            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT318L));
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(getLiteForDevice(device));

            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT310));
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
    public void testGetPointTemplatesToTransfer() {

        // Test remove points from type 'device1' to type 'device2'
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();
        expectedTemplates.add(new PointTemplateImpl("pulse2",
                                                    2,
                                                    4,
                                                    1.0,
                                                    1,
                                                    0,
                                                    false,
                                                    new UserDefinedAttribute("pulse2")));

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
            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT318L));
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(getLiteForDevice(device));

            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT310));
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
    public void testGetNewPointTemplatesForTransfer() {

        // Test remove points from type 'device1' to type 'device2'
        Set<PointTemplate> expectedTemplates = new HashSet<PointTemplate>();
        expectedTemplates.add(new PointTemplateImpl("pulse2",
                                                    2,
                                                    3,
                                                    0.1,
                                                    1,
                                                    0,
                                                    false,
                                                    new UserDefinedAttribute("pulse2")));

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
            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT318L));
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(getLiteForDevice(device));

            device.setDeviceType(PAOGroups.getPAOTypeString(DeviceTypes.MCT310));
            service.getNewPointTemplatesForTransfer(device, deviceDefinition);
            fail("getPointTemplatesToTransfer should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }
    
    private LiteYukonPAObject getLiteForDevice(DeviceBase deviceBase) {
        return (LiteYukonPAObject) LiteFactory.createLite(deviceBase);
    }

}
