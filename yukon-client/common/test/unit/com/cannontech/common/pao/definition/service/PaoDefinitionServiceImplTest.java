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
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest.MockEmptyDeviceDefinitionDao;
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
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao(new MockEmptyDeviceDefinitionDao());
        service.setPaoDefinitionDao(paoDefinitionDao);

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
        expectedPoints.add(pointCreationService.createPoint(2, "pulse1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(3, "demand1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(1, "analog1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));

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
        assertTrue("device3 is not changeable", !service.isPaoTypeChangeable(device));

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
        Set<PaoDefinition> expectedPaos = new HashSet<PaoDefinition>();
        SimpleDevice device2 = new SimpleDevice(11, DeviceTypes.MCT370);
        expectedPaos.add(paoDefinitionDao.getPaoDefinition(device2.getDeviceType()));

        Set<PaoDefinition> actualPaos = service.getChangeablePaos(device);

        assertEquals("Changeable devices were not as expected", expectedPaos, actualPaos);

        // Test with device that is not changeable
        device = new SimpleDevice(1, DeviceTypes.MCT318L);
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
        expectedPoints.add(pointCreationService.createPoint(0, "status1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "pulse1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(2, "pulse2", new PaoIdentifier(1, PaoType.MCT310), 2, 0.1, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(3, "demand1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 0, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));
        expectedPoints.add(pointCreationService.createPoint(1, "analog1", new PaoIdentifier(1, PaoType.MCT310), 1, 1.0, 1, 0, 0, 3, StatusControlType.NONE, PointArchiveType.NONE, PointArchiveInterval.ZERO));

        List<PointBase> actualPoints = service.createAllPointsForPao(device);

        assertEquals("All points weren't as expected", expectedPoints, actualPoints);
    }
}
