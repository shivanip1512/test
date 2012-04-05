package com.cannontech.common.pao.definition.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import com.cannontech.common.config.ConfigResourceLoader;
import com.cannontech.common.config.retrieve.ConfigFile;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinitionImpl;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.definition.service.PaoDefinitionService.PointTemplateTransferPair;
import com.cannontech.common.pao.service.impl.PointCreationServiceImpl;
import com.cannontech.common.pao.service.impl.PointServiceImpl;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Sets;

/**
 * Test class for DeviceDefinitionService
 */
public class PaoDefinitionServiceImplTest {

    private PaoDefinitionServiceImpl service = null;
    private PointServiceImpl pointService = null;
    private PointCreationServiceImpl pointCreationService = null;
    private SimpleDevice device = null;
    private PaoDefinitionDao paoDefinitionDao = null;
    private AttributeServiceImpl attributeService = null;
    private PointDao pointDao;
    
    @Before
    public void setUp() throws Exception {

        service = new PaoDefinitionServiceImpl();
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao(new ConfigResourceLoader() {
            public Resource getResource(ConfigFile config) {
                return null;
            }
        });
        service.setPaoDefinitionDao(paoDefinitionDao);

        pointDao = new MockPointDao();
        
        pointService = new PointServiceImpl();
        pointCreationService = new PointCreationServiceImpl();
        // Create the point service for testing
        pointService.setPointDao(pointDao);
        pointCreationService.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        service.setPointCreationService(pointCreationService);
        service.setPointDao(pointDao);

        // Create the attribute service for testing
        attributeService = new AttributeServiceImpl();
        attributeService.setPaoDefinitionDao(paoDefinitionDao);
        attributeService.setPointService(pointService);

        device = new SimpleDevice(1, DeviceTypes.MCT310);

    }

    /**
     * Test createDefaultPointsForDevice()
     */
    @Test
    public void testCreateDefaultPointsForPao() {

        // Test with supported device
        List<PointBase> expectedPoints = new ArrayList<PointBase>();
        expectedPoints.add(pointCreationService.createPoint(2, "pulse1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(3, "demand1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(1, "analog1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));

        List<PointBase> actualPoints = service.createDefaultPointsForPao(device);

        assertEquals("Default points weren't as expected", expectedPoints, actualPoints);

        // Test with unsupported device
        try {
            device.setType(9999999);
            actualPoints = service.createDefaultPointsForPao(device);
            fail("createDefaultPointsForPao should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test isPaoTypeChangeable()
     */
    @Test
    public void testIsPaoTypeChangeable() {

        // Test with changeable device
        assertTrue("device1 is changeable", service.isPaoTypeChangeable(device));

        // Test with device that is not changeable
        device.setType(DeviceTypes.MCT318L);
        assertTrue("device3 is not changeable", !service.isPaoTypeChangeable(device));

        // Test with unsupported device
        try {
            device.setType(999999);
            service.isPaoTypeChangeable(device);
            fail("isPaoTypeChangeable should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test getChangeablePaos()
     */
    @Test
    public void testGetChangeablePaos() {

        // Test with changeable device
        Set<PaoDefinition> expectedPaos = new HashSet<PaoDefinition>();
        SimpleDevice device2 = new SimpleDevice(11, DeviceTypes.MCT370);
        expectedPaos.add(paoDefinitionDao.getPaoDefinition(device2.getDeviceType()));

        Set<PaoDefinition> actualPaos = service.getChangeablePaos(device);

        assertEquals("Changeable devices were not as expected", expectedPaos, actualPaos);

        // Test with device that is not changeable
        device.setType(DeviceTypes.MCT318L);
        Set<PaoDefinition> actualDevices2 = service.getChangeablePaos(device);
        assertTrue("Should be empty set", actualDevices2.isEmpty());

        // Test with unsupported device
        try {
            device.setType(999999);
            service.getChangeablePaos(device);
            fail("getChangeablePaos should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    /**
     * Test createAllPointsForPao()
     */
    @Test
    public void testCreateAllPointsForPao() {

        // Test with supported device
        List<PointBase> expectedPoints = new ArrayList<PointBase>();
        expectedPoints.add(pointCreationService.createPoint(0, "status1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "pulse1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "pulse2", new PaoIdentifier(1, PaoType.MCT310), 2, 0.1, 1, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(3, "demand1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(1, "analog1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, ControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));

        List<PointBase> actualPoints = service.createAllPointsForPao(device);

        assertEquals("All points weren't as expected", expectedPoints, actualPoints);

        // Test with unsupported device
        try {
            device.setType(999999);
            actualPoints = service.createAllPointsForPao(device);
            fail("createAllPointsForPao should've thrown an exception");
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

        
        Set<PointTemplate> actualTemplates = service.getPointTemplatesToAdd(device,
                                                                            new PaoDefinitionImpl(PaoType.getForId(1019),
                                                                                                     "Device1",
                                                                                                     "display1",
                                                                                                     "MCT310",
                                                                                                     "change1",
                                                                                                     true));

        assertEquals("Point templates to add were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test add points from type 'device1' to type 'device3' (is an invalid
        // change)
        try {
            device.setType(DeviceTypes.MCT318L);
            PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(device.getDeviceType());

            device.setType(DeviceTypes.MCT310);
            service.getPointTemplatesToAdd(device, paoDefinition);
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
        Set<PointIdentifier> expectedTemplates = new HashSet<PointIdentifier>();

        // Pulse Accumulators - "pulse1"
        expectedTemplates.add(new PointIdentifier(2, 2));

        // Demand Accumulators - "demand1"
        expectedTemplates.add(new PointIdentifier(3, 1));

        Set<PointIdentifier> actualTemplates = service.getPointTemplatesToRemove(device,
                                                                               new PaoDefinitionImpl(PaoType.getForId(1022),
                                                                                                        "Device2",
                                                                                                        "display2",
                                                                                                        "MCT370",
                                                                                                        "change1",
                                                                                                        true));

        assertEquals("Point templates to remove were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test remove points from type 'device1' to type 'device3' (is an
        // invalid change)
        try {
            device.setType(DeviceTypes.MCT318L);
            PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(device.getDeviceType());

            device.setType(DeviceTypes.MCT310);
            service.getPointTemplatesToRemove(device, paoDefinition);
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
    	Set<PointTemplateTransferPair> expectedTemplates = Sets.newHashSet();

        // Analog
        PointTemplateTransferPair pair = new PointTemplateTransferPair();
        pair.newDefinitionTemplate = new PointTemplate("pulse2", PointType.PulseAccumulator, 3, .1, 1, 0, 3);
        pair.oldDefinitionTemplate = new PointIdentifier(PointType.PulseAccumulator, 4);
        expectedTemplates.add(pair);

        pair = new PointTemplateTransferPair();
        pair.newDefinitionTemplate = new PointTemplate("analog1", PointType.Analog, 1, .0001, 1, 0, 3);
        pair.oldDefinitionTemplate = new PointIdentifier(PointType.Analog, 1);
        expectedTemplates.add(pair);
        PaoDefinitionImpl newDefinition = new PaoDefinitionImpl(PaoType.MCT370,
                                                                      "Device2",
                                                                      "display2",
                                                                      "MCT370",
                                                                      "change1",
                                                                      true);
        Set<PointTemplateTransferPair> actualTemplates = service.getPointTemplatesToTransfer(device,
                                                                                              newDefinition);

        assertEquals("Point templates to transfer were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test transfer points from type 'device1' to type 'device3' (is an
        // invalid change)
        try {
            device.setType(DeviceTypes.MCT318L);
            PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(device.getDeviceType());

            device.setType(DeviceTypes.MCT310);
            service.getPointTemplatesToTransfer(device, paoDefinition);
            fail("getPointTemplatesToTransfer should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }
}
