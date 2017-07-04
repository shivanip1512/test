package com.cannontech.common.pao.definition.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.service.impl.PointCreationServiceImpl;
import com.cannontech.common.pao.service.impl.PointServiceImpl;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.incrementer.NextValueHelper;

public class PaoDefinitionServiceImplTest {

    private PaoDefinitionServiceImpl service;
    private PointServiceImpl pointService;
    private PointCreationServiceImpl pointCreationService;
    private SimpleDevice device;
    private PaoDefinitionDao paoDefinitionDao;
    private AttributeServiceImpl attributeService;
    private PointDao pointDao;

    @Before
    public void setUp() throws Exception {
        service = new PaoDefinitionServiceImpl();
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
        ReflectionTestUtils.setField(service, "paoDefinitionDao", paoDefinitionDao);
        pointDao = new MockPointDao();
        pointService = new PointServiceImpl();
        pointCreationService = new PointCreationServiceImpl();
        // Create the point service for testing
        ReflectionTestUtils.setField(pointService, "pointDao", pointDao);
        pointCreationService.setNextValueHelper(new NextValueHelper() {
            @Override
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        service.setPointCreationService(pointCreationService);
        service.setPointDao(pointDao);

        // Create the attribute service for testing
        attributeService = new AttributeServiceImpl();
        ReflectionTestUtils.setField(attributeService, "paoDefinitionDao", paoDefinitionDao);
        ReflectionTestUtils.setField(attributeService, "pointService", pointService);

        device = new SimpleDevice(1, DeviceTypes.MCT310);
    }

    /**
     * Test createDefaultPointsForDevice()
     */
    @Test
    public void testCreateDefaultPointsForPao() {
        // Test with supported device
        List<PointBase> expectedPoints = new ArrayList<PointBase>();
        expectedPoints.add(pointCreationService.createPoint(2, "kWh", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1,
            0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "Blink Count", new PaoIdentifier(1, PaoType.MCT310), 1,
            1.0, 0, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(0, "Power Fail", new PaoIdentifier(1, PaoType.MCT310), 1,
            1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(0, "Outage Status", new PaoIdentifier(1, PaoType.MCT310),
            1, 1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));

        List<PointBase> actualPoints = service.createDefaultPointsForPao(device);

        assertEquals("Default points weren't as expected", expectedPoints, actualPoints);

        // Test with unsupported device
        try {
            device = new SimpleDevice(1, 9999999);
            fail("new SimpleDevice should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
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
        device = new SimpleDevice(1, DeviceTypes.MCT318L);
        assertTrue("device3 is not changeable", service.isPaoTypeChangeable(device));

        // Test with unsupported device
        try {
            device = new SimpleDevice(1, 999999);
            fail("new SimpleDevice should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    /**
     * Test getChangeablePaos()
     */
    @Test
    public void testGetChangeablePaos() {
        // Test with changeable device
        // Set<PaoDefinition> expectedPaos = new HashSet<PaoDefinition>();
        SimpleDevice device1 = new SimpleDevice(11, DeviceTypes.MCT370);
        PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(device1.getDeviceType());

        SimpleDevice device2 = new SimpleDevice(12, DeviceTypes.MCT210);
        PaoDefinition paoDefinition2 = paoDefinitionDao.getPaoDefinition(device2.getDeviceType());

        Set<PaoDefinition> actualPaos = service.getChangeablePaos(device);

        assertTrue(actualPaos.contains(paoDefinition));
        assertTrue(actualPaos.contains(paoDefinition2));

        // Test with device that is not changeable
        device = new SimpleDevice(1, DeviceTypes.LCR6200_RFN);
        Set<PaoDefinition> actualDevices2 = service.getChangeablePaos(device);
        assertTrue("Should be empty set", actualDevices2.isEmpty());
    }

    /**
     * Test createAllPointsForPao()
     */
    @Test
    public void testCreateAllPointsForPao() {
        // Test with supported device
        List<PointBase> expectedPoints = new ArrayList<PointBase>();
        expectedPoints.add(pointCreationService.createPoint(2, "kWh", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0,
            0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "Blink Count", new PaoIdentifier(1, PaoType.MCT310), 1,
            1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "Comm Status", new PaoIdentifier(1, PaoType.MCT310),
            2000, 0.1, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "Short Power Fail Flag", new PaoIdentifier(1,
            PaoType.MCT310), 11, 0.1, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE,
            PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "Over Flow Flag", new PaoIdentifier(1, PaoType.MCT310),
            12, 0.1, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(0, "Power Fail", new PaoIdentifier(1, PaoType.MCT310), 1,
            1.0, 0, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(0, "Outage Status", new PaoIdentifier(1, PaoType.MCT310),
            1, 1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));

        List<PointBase> actualPoints = service.createAllPointsForPao(device);

        assertEquals("All points weren't as expected", expectedPoints, actualPoints);
    }

}
