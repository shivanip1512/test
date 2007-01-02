package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.device.attribute.service.AttributeServiceImpl;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDaoImplTest;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointServiceImpl;
import com.cannontech.common.mock.MockDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.mock.OLDDeviceChngTypesPanel;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAccumulator;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointAnalog;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

/**
 * Test class for DeviceDefinitionService
 */
public class DeviceDefinitionServiceImplTest extends TestCase {

    private DeviceDefinitionServiceImpl service = null;
    PointServiceImpl pointService = null;
    private DeviceBase device = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private AttributeServiceImpl attributeService = null;

    protected void setUp() throws Exception {

        service = new DeviceDefinitionServiceImpl();
        deviceDefinitionDao = DeviceDefinitionDaoImplTest.getTestDeviceDefinitionDao();
        service.setDeviceDefinitionDao(deviceDefinitionDao);

        // Create the point service for testing
        pointService = new PointServiceImpl();
        pointService.setNextValueHelper(new NextValueHelper() {
            public int getNextValue(String tableName) {
                return 1;
            }
        });
        service.setPointService(pointService);

        // Create the attribute service for testing
        attributeService = new AttributeServiceImpl();
        attributeService.setDeviceDefinitionDao(deviceDefinitionDao);
        attributeService.setPointDao(new MockPointDao());
        service.setAttributeService(attributeService);

        device = new MockDevice();
        device.setDeviceType("device1");
        device.setPAOName("Test Device");
        device.setDeviceID(1);

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
        device.setDeviceType("device3");
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
        device2.setDeviceType("device2");
        expectedDevices.add(deviceDefinitionDao.getDeviceDefinition(device2));

        Set<DeviceDefinition> actualDevices = service.getChangeableDevices(device);

        assertEquals("Changeable devices were not as expected", expectedDevices, actualDevices);

        // Test with device that is not changeable
        try {
            device.setDeviceType("device3");
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
        device.setDeviceType("device2");
        Set<PointTemplate> expectedTemplates = DeviceDefinitionDaoImplTest.getExpectedInitTemplates();

        Set<PointTemplate> actualTemplates = service.getPointTemplatesToAdd(device,
                                                                            new DeviceDefinition(1,
                                                                                                 "Device1",
                                                                                                 "display1",
                                                                                                 "constant1",
                                                                                                 "change1"));

        assertEquals("Point templates to add were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test add points from type 'device1' to type 'device3' (is an invalid
        // change)
        try {
            device.setDeviceType("device3");
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

            device.setDeviceType("device1");
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
                                                                               new DeviceDefinition(2,
                                                                                                    "Device2",
                                                                                                    "display2",
                                                                                                    "constant2",
                                                                                                    "change1"));

        assertEquals("Point templates to remove were not as expected",
                     expectedTemplates,
                     actualTemplates);

        // Test remove points from type 'device1' to type 'device3' (is an
        // invalid change)
        try {
            device.setDeviceType("device3");
            DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

            device.setDeviceType("device1");
            service.getPointTemplatesToRemove(device, deviceDefinition);
            fail("getPointTemplatesToRemove should've thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        } catch (Exception e) {
            fail("Threw wrong type of exception: " + e.getClass());
        }

    }

    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    /***************************************************************************
     * All code below is to test that the new point creation code is working
     * exactly as the old point creation code did. These tests should be removed
     * when initial device definition development is complete
     **************************************************************************/
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    private int[] devices = new int[] { DeviceTypes.MCT210, DeviceTypes.MCT213, DeviceTypes.MCT240,
            DeviceTypes.MCT248, DeviceTypes.MCT250, DeviceTypes.MCT310, DeviceTypes.MCT310ID,
            DeviceTypes.MCT310IDL, DeviceTypes.MCT310IL, DeviceTypes.MCT310CT,
            DeviceTypes.MCT310IM, DeviceTypes.MCT318, DeviceTypes.MCT318L, DeviceTypes.MCT360,
            DeviceTypes.MCT370, DeviceTypes.MCT410CL, DeviceTypes.MCT410IL, DeviceTypes.MCT430A,
            DeviceTypes.MCT430S, DeviceTypes.MCT470, DeviceTypes.LMT_2, DeviceTypes.DCT_501 };

    public void testOLDCreateDefaultPointsForDevice() {

        DeviceBase device = null;

        for (int deviceType : devices) {
            device = this.createNewDevice(deviceType);

            List<PointBase> expectedPoints = this.getPointUtilPointList(device);
            Collections.sort(expectedPoints, new PointComparator());
            boolean supported = expectedPoints.size() != 0;

            DeviceDefinitionService defService = (DeviceDefinitionService) YukonSpringHook.getBean("deviceService");
            List<PointBase> actualPoints = defService.createDefaultPointsForDevice(device);
            Collections.sort(actualPoints, new PointComparator());

            boolean equal = supported && this.comparePointLists(expectedPoints, actualPoints);

            if (supported) {
                System.out.println(device.getPAOType() + ": " + ((equal) ? "" : "don't") + " match");

                if (!equal) {

                    System.out.println("Expected:");
                    for (PointBase expectedPoint : expectedPoints) {
                        System.out.println("\t" + expectedPoint.getPoint().getPointName());
                    }
                    System.out.println("Actual:");
                    for (PointBase actualPoint : actualPoints) {
                        System.out.println("\t" + actualPoint.getPoint().getPointName());
                    }
                    fail(device.getPAOType() + " points don't match");

                }
            } else {
                System.out.println(device.getPAOType() + ": not supported by PointUtil");

            }
        }
    }

    public void testOLDIsDeviceTypeChangeable() {

        String[] changeableDeviceNameArray = new String[] { PAOGroups.STRING_MCT_470[0],
                PAOGroups.STRING_MCT_410IL[0], PAOGroups.STRING_MCT_410CL[0],
                PAOGroups.STRING_MCT_370[0], PAOGroups.STRING_MCT_360[0],
                PAOGroups.STRING_MCT_318L[0], PAOGroups.STRING_MCT_318[0],
                PAOGroups.STRING_MCT_310CT[0], PAOGroups.STRING_MCT_310ID[0],
                PAOGroups.STRING_MCT_310IDL[0], PAOGroups.STRING_MCT_310IL[0],
                PAOGroups.STRING_MCT_310IM[0], PAOGroups.STRING_MCT_310[0],
                PAOGroups.STRING_MCT_250[0], PAOGroups.STRING_MCT_248[0],
                PAOGroups.STRING_MCT_240[0], PAOGroups.STRING_MCT_213[0],
                PAOGroups.STRING_MCT_210[0], PAOGroups.STRING_LMT_2[0],
                PAOGroups.STRING_DCT_501[0], PAOGroups.STRING_CCU_710[0],
                PAOGroups.STRING_CCU_711[0], PAOGroups.STRING_LCU_415[0],
                PAOGroups.STRING_LCU_LG[0], PAOGroups.STRING_TCU_5000[0],
                PAOGroups.STRING_TCU_5500[0], PAOGroups.STRING_ALPHA_POWERPLUS[0],
                PAOGroups.STRING_ALPHA_A1[0], PAOGroups.STRING_DR_87[0],
                PAOGroups.STRING_FULCRUM[0], PAOGroups.STRING_LANDISGYR_RS4[0],
                PAOGroups.STRING_QUANTUM[0], PAOGroups.STRING_TRANSDATA_MARKV[0],
                PAOGroups.STRING_VECTRON[0], PAOGroups.STRING_RTU_ILEX[0],
                PAOGroups.STRING_RTU_WELCO[0], PAOGroups.STRING_EMETCON_GROUP[0],
                PAOGroups.STRING_EXPRESSCOMM_GROUP[0], PAOGroups.STRING_VERSACOM_GROUP[0],
                PAOGroups.STRING_REPEATER[1], PAOGroups.STRING_REPEATER_800[0],
                PAOGroups.STRING_CAP_BANK_CONTROLLER[0], PAOGroups.STRING_CBC_7010[0],
                PAOGroups.STRING_CBC_EXPRESSCOM[0] };
        System.out.println("\n\n\n");
        for (String deviceName : changeableDeviceNameArray) {
            boolean changeable = OLDDeviceChngTypesPanel.isDeviceTypeChangeable(deviceName);

            DeviceBase device = DeviceFactory.createDevice(PAOGroups.getDeviceType(deviceName));
            DeviceDefinitionService defService = (DeviceDefinitionService) YukonSpringHook.getBean("deviceService");
            boolean newChangeable = defService.isDeviceTypeChangeable(device);
            System.out.println(deviceName + ": " + changeable + ", " + newChangeable);

            assertEquals("Device: " + deviceName + " chaneable not set correctly ",
                         changeable,
                         newChangeable);
        }

        // service.isDeviceTypeChangeable(null);

    }

    private List<PointBase> getPointUtilPointList(DeviceBase device) {

        List<PointBase> pointList = new ArrayList<PointBase>();

        MultiDBPersistent pointPersistant = (MultiDBPersistent) PointUtil.generatePointsForMCT(device);
        if (pointPersistant != null) {
            for (Object object : pointPersistant.getDBPersistentVector()) {
                if (object instanceof PointBase) {
                    pointList.add((PointBase) object);
                }
            }
        }
        return pointList;
    }

    private DeviceBase createNewDevice(int deviceType) {

        DeviceBase device = DeviceFactory.createDevice(deviceType);

        device.setDeviceID(1);
        device.setDeviceType(PAOGroups.getPAOTypeString(deviceType));
        return device;
    }

    private boolean comparePointLists(List<PointBase> points1, List<PointBase> points2) {

        if (points1.size() != points2.size()) {
            System.out.println(points1.size() + ", " + points2.size());
            return false;
        }

        for (int i = 0; i < points1.size(); i++) {

            PointBase point1 = points1.get(i);
            PointBase point2 = points2.get(i);

            if (point1 instanceof AnalogPoint && point2 instanceof AnalogPoint) {

                AnalogPoint analog1 = (AnalogPoint) point1;
                AnalogPoint analog2 = (AnalogPoint) point2;

                if (!this.comparePoint(analog1.getPoint(), analog2.getPoint())
                        || !this.comparePointUnit(analog1.getPointUnit(), analog2.getPointUnit())
                        || !this.comparePointAnalog(analog1.getPointAnalog(),
                                                    analog2.getPointAnalog())
                        || !this.comparePointAlarming(analog1.getPointAlarming(),
                                                      analog2.getPointAlarming())) {
                    System.out.println(analog1.getPoint().getPointName() + " doesn't match");
                    return false;
                }

            } else if (point1 instanceof AccumulatorPoint && point2 instanceof AccumulatorPoint) {
                AccumulatorPoint accum1 = (AccumulatorPoint) point1;
                AccumulatorPoint accum2 = (AccumulatorPoint) point2;

                if (!this.comparePoint(accum1.getPoint(), accum2.getPoint())
                        || !this.comparePointUnit(accum1.getPointUnit(), accum2.getPointUnit())
                        || !this.comparePointAccumulator(accum1.getPointAccumulator(),
                                                         accum2.getPointAccumulator())) {
                    System.out.println(accum1.getPoint().getPointName() + " doesn't match");
                    return false;
                }
            } else if (point1 instanceof StatusPoint && point2 instanceof StatusPoint) {
                StatusPoint status1 = (StatusPoint) point1;
                StatusPoint status2 = (StatusPoint) point2;

                if (!this.comparePoint(status1.getPoint(), status2.getPoint())
                        || !this.comparePointStatus(status1.getPointStatus(),
                                                    status2.getPointStatus())
                        || !this.comparePointAlarming(status1.getPointAlarming(),
                                                      status2.getPointAlarming())) {
                    System.out.println(status1.getPoint().getPointName() + " doesn't match");
                    return false;
                }

            }
        }

        return true;
    }

    private boolean comparePoint(Point point1, Point point2) {
        if (point1 == null && point2 == null) {
            return true;
        }
        if (!(point1.getPointType().equals(point2.getPointType()))
                || (point1.getPaoID().intValue() != point2.getPaoID().intValue())
                || !(point1.getLogicalGroup().equals(point2.getLogicalGroup()))
                || (point1.getStateGroupID().intValue() != point2.getStateGroupID().intValue())
                || (point1.getServiceFlag().charValue() != point2.getServiceFlag().charValue())
                || (point1.getAlarmInhibit().charValue() != point2.getAlarmInhibit().charValue())
                || (point1.getPointOffset().intValue() != point2.getPointOffset().intValue())
                || !(point1.getArchiveType().equals(point2.getArchiveType()))
                || (point1.getArchiveInterval().intValue() != point2.getArchiveInterval()
                                                                    .intValue())) {
            return false;
        }
        return true;
    }

    private boolean comparePointUnit(PointUnit pointUnit1, PointUnit pointUnit2) {
        if (pointUnit1 == null && pointUnit2 == null) {
            return true;
        }
        if ((pointUnit1.getUomID().intValue() != pointUnit2.getUomID().intValue())
                || (pointUnit1.getDecimalPlaces().intValue() != pointUnit2.getDecimalPlaces()
                                                                          .intValue())
                || (pointUnit1.getHighReasonabilityLimit().doubleValue() != pointUnit2.getHighReasonabilityLimit()
                                                                                      .doubleValue())
                || (pointUnit1.getLowReasonabilityLimit().doubleValue() != pointUnit2.getLowReasonabilityLimit()
                                                                                     .doubleValue())
                || (pointUnit1.getMeterDials().intValue() != pointUnit2.getMeterDials().intValue())) {
            return false;
        }
        return true;
    }

    private boolean comparePointAlarming(PointAlarming pointAlarming1, PointAlarming pointAlarming2) {
        if (pointAlarming1 == null && pointAlarming2 == null) {
            return true;
        }
        if ((!pointAlarming1.getAlarmStates().equals(pointAlarming2.getAlarmStates()))
                || (!pointAlarming1.getExcludeNotifyStates()
                                   .equals(pointAlarming2.getExcludeNotifyStates()))
                || (!pointAlarming1.getNotifyOnAcknowledge()
                                   .equals(pointAlarming2.getNotifyOnAcknowledge()))
                || (pointAlarming1.getNotificationGroupID().intValue() != pointAlarming2.getNotificationGroupID()
                                                                                        .intValue())
                || (pointAlarming1.getRecipientID().intValue() != pointAlarming2.getRecipientID()
                                                                                .intValue())) {
            return false;
        }
        return true;
    }

    private boolean comparePointAnalog(PointAnalog pointAnalog1, PointAnalog pointAnalog2) {
        if (pointAnalog1 == null && pointAnalog2 == null) {
            return true;
        }
        if ((pointAnalog1.getDeadband().doubleValue() != pointAnalog2.getDeadband().doubleValue())
                || (!pointAnalog1.getTransducerType().equals(pointAnalog2.getTransducerType()))
                || (pointAnalog1.getMultiplier().doubleValue() != pointAnalog2.getMultiplier()
                                                                              .doubleValue())
                || (pointAnalog1.getDataOffset().doubleValue() != pointAnalog2.getDataOffset()
                                                                              .doubleValue())) {
            return false;
        }
        return true;
    }

    private boolean comparePointAccumulator(PointAccumulator pointAccumulator1,
            PointAccumulator pointAccumulator2) {
        if (pointAccumulator1 == null && pointAccumulator2 == null) {
            return true;
        }
        if ((pointAccumulator1.getMultiplier().doubleValue() != pointAccumulator2.getMultiplier()
                                                                                 .doubleValue())
                || (pointAccumulator1.getDataOffset().doubleValue() != pointAccumulator2.getDataOffset()
                                                                                        .doubleValue())) {
            return false;
        }
        return true;
    }

    private boolean comparePointStatus(PointStatus pointStatus1, PointStatus pointStatus2) {
        if (pointStatus1 == null && pointStatus2 == null) {
            return true;
        }
        if ((pointStatus1.getInitialState().intValue() != pointStatus2.getInitialState().intValue())
                || !(pointStatus1.getControlType().equals(pointStatus2.getControlType()))
                || (pointStatus1.getControlInhibit().charValue() != pointStatus2.getControlInhibit()
                                                                                .charValue())
                || (pointStatus1.getControlOffset().intValue() != pointStatus2.getControlOffset()
                                                                              .intValue())
                || (pointStatus1.getCloseTime1().intValue() != pointStatus2.getCloseTime1()
                                                                           .intValue())
                || (pointStatus1.getCloseTime2().intValue() != pointStatus2.getCloseTime2()
                                                                           .intValue())
                || !(pointStatus1.getStateZeroControl().equals(pointStatus2.getStateZeroControl()))
                || !(pointStatus1.getStateOneControl().equals(pointStatus2.getStateOneControl()))
                || (pointStatus1.getCommandTimeOut().intValue() != pointStatus2.getCommandTimeOut()
                                                                               .intValue())) {
            return false;
        }
        return true;
    }

    private class PointComparator implements Comparator<PointBase> {

        public int compare(PointBase o1, PointBase o2) {

            return o1.getPoint().getPointName().compareTo(o2.getPoint().getPointName());
        }

    }
}
