package com.cannontech.services.deviceDataMonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.deviceDataMonitor.service.impl.DeviceDataMonitorCalculationServiceImpl;
import com.cannontech.amr.monitors.impl.DeviceDataMonitorProcessorFactoryImpl;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.impl.DeviceGroupProviderDaoMain;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.impl.DeviceGroupEditorDaoImpl;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.impl.DeviceGroupServiceImpl;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.impl.AsyncDynamicDataSourceImpl;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class DeviceDataMonitorTest {

    private DeviceDataMonitorProcessorFactoryImpl deviceDataMonitorProcessorFactory;
    private AttributeService attributeService;
    private DeviceDataMonitorCalculationService deviceDataMonitorCalculationService;
    private DeviceGroupService deviceGroupService;
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupProviderDao deviceGroupDao;

    private final static LiteState STATE_1 = new LiteState(1, null, 0, 0, 0);
    private final static LiteState STATE_2 = new LiteState(2, null, 0, 0, 0);
    private final static LiteStateGroup STATE_GROUP_1 = new LiteStateGroup(1);
    private final static LiteStateGroup STATE_GROUP_2 = new LiteStateGroup(2);
    
    private final static PaoIdentifier PAO_IDENTIFIER_1 = new PaoIdentifier(1, PaoType.MCT410CL);
    private final static PaoIdentifier PAO_IDENTIFIER_2 = new PaoIdentifier(2, PaoType.MCT410CL);
    private final static PaoIdentifier PAO_IDENTIFIER_5 = new PaoIdentifier(5, PaoType.MCT410CL);

    private final static LitePoint point1 = new LitePoint(1, "point1", PointType.Status.getPointTypeId(), PAO_IDENTIFIER_1.getPaoId(), 1, STATE_1.getLiteID());
    private final static LitePoint point2 = new LitePoint(2, "point2", PointType.Status.getPointTypeId(), PAO_IDENTIFIER_2.getPaoId(), 2, STATE_1.getLiteID());
    private final static LitePoint point5 =  new LitePoint(5, "point5", PointType.DemandAccumulator.getPointTypeId(), PAO_IDENTIFIER_5.getPaoId(), 5, 0);

    private final static Map<Integer, LitePoint> deviceIdToLitePointMap = ImmutableMap.of(PAO_IDENTIFIER_1.getPaoId(), point1, PAO_IDENTIFIER_2.getPaoId(), point2, PAO_IDENTIFIER_5.getPaoId(), point5);


    private final static PointIdentifier POINT_IDENTIFIER_1 = new PointIdentifier(PointType.getForId(point1.getPointType()), point1.getPointOffset());
    private final static PointIdentifier POINT_IDENTIFIER_2 = new PointIdentifier(PointType.getForId(point2.getPointType()), point2.getPointOffset());
    private final static PointIdentifier POINT_IDENTIFIER_5 = new PointIdentifier(PointType.getForId(point5.getPointType()), point5.getPointOffset());

    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER_1 = new PaoPointIdentifier(PAO_IDENTIFIER_1, POINT_IDENTIFIER_1);
    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER_2 = new PaoPointIdentifier(PAO_IDENTIFIER_2, POINT_IDENTIFIER_2);
    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER_5 = new PaoPointIdentifier(PAO_IDENTIFIER_1, POINT_IDENTIFIER_5);

    private final DeviceGroupTest MONITORING_GROUP_1 = new DeviceGroupTest("monitoring_1");

    private final DeviceGroupTest VIOLATIONS_GROUP_1 = new DeviceGroupTest("violations_1");

    private Map<String, HashSet<PaoIdentifier>> deviceGroupPaos = new HashMap<>();
    private List<RichPointData> pointDatas = new ArrayList<>();
    private BuiltInAttribute attribute;

    @Before
    public void setup() {
        attributeService = new AttributeServiceImpl() {
            @Override
            public Multimap<SimpleDevice, Attribute> getDevicesInGroupThatSupportAttribute(DeviceGroup group,
                    List<BuiltInAttribute> attributes, List<Integer> deviceIds) {
                Multimap<SimpleDevice, Attribute> attributeToDevice = HashMultimap.create();
                pointDatas.forEach(p -> {
                    attributeToDevice.put(new SimpleDevice(p.getPaoPointIdentifier().getPaoIdentifier()), attribute);
                });
                return attributeToDevice;
            }

            @Override
            public BiMap<PaoIdentifier, LitePoint> getPoints(Iterable<? extends YukonPao> devices,
                    BuiltInAttribute attribute) {
                BiMap<PaoIdentifier, LitePoint> points = HashBiMap.create();
                pointDatas.forEach(p -> {
                    points.put(p.getPaoPointIdentifier().getPaoIdentifier(),
                        deviceIdToLitePointMap.get(p.getPaoPointIdentifier().getPaoIdentifier().getPaoId()));
                });
                return points;
            }
        };

        deviceGroupService = new DeviceGroupServiceImpl() {
            @Override
            public boolean isDeviceInGroup(DeviceGroup group, YukonPao pao) {
                HashSet<? extends YukonPao> paos = deviceGroupPaos.get(group.getName());
                if (paos == null) {
                    return false;
                }
                return paos.contains(pao.getPaoIdentifier());
            }
        };

        deviceGroupMemberEditorDao = new DeviceGroupEditorDaoImpl() {
            @Override
            public void addDevices(StoredDeviceGroup group, YukonPao... pao) {
                List<PaoIdentifier> paos =
                    Lists.newArrayList(pao).stream().map(a -> a.getPaoIdentifier()).collect(Collectors.toList());
                HashSet<PaoIdentifier> devices = deviceGroupPaos.getOrDefault(group.getName(), new HashSet<>());
                devices.addAll(paos);
                deviceGroupPaos.put(group.getName(), devices);
            }

            @Override
            public int removeDevicesById(StoredDeviceGroup group, Collection<Integer> deviceIds) {
                int numAffected = deviceIds.size();
                HashSet<PaoIdentifier> paoIds = deviceGroupPaos.get(group.getName());
                for (YukonPao yukonPao : paoIds) {
                    if (deviceIds.contains(yukonPao.getPaoIdentifier().getPaoId())) {
                        deviceGroupPaos.get(group.getName()).remove(yukonPao.getPaoIdentifier());
                        numAffected++;
                    }
                }
                return numAffected;
            }
        };

        deviceGroupDao = new DeviceGroupProviderDaoMain() {
            @Override
            public int getDeviceCount(DeviceGroup group) {
                HashSet<PaoIdentifier> hashSet = deviceGroupPaos.getOrDefault(group.getName(), new HashSet<>());
                return hashSet.size();
            }
        };

        asyncDynamicDataSource = new AsyncDynamicDataSourceImpl() {
            @Override
            public Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds) {
                return pointDatas.stream().filter(p -> pointIds.contains(p.getPointValue().getId())).map(
                    p -> p.getPointValue()).collect(Collectors.toSet());
            }
        };

        deviceDataMonitorCalculationService = new DeviceDataMonitorCalculationServiceImpl() {

            @Override
            public void sendSmartNotificationEvent(DeviceDataMonitor monitor, int deviceId, MonitorState state) {
            }

            @Override
            public void updateViolationCacheCount(DeviceDataMonitor monitor) {

            }

        };
        ReflectionTestUtils.setField(deviceDataMonitorCalculationService, "attributeService", attributeService);
        ;
        ReflectionTestUtils.setField(deviceDataMonitorCalculationService, "deviceGroupMemberEditorDao",
            deviceGroupMemberEditorDao);
        ReflectionTestUtils.setField(deviceDataMonitorCalculationService, "asyncDynamicDataSource",
            asyncDynamicDataSource);

        ReflectionTestUtils.setField(deviceDataMonitorCalculationService, "deviceGroupService", deviceGroupService);
        deviceDataMonitorProcessorFactory = new DeviceDataMonitorProcessorFactoryImpl();
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "deviceGroupService", deviceGroupService);
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "deviceDataMonitorCalculationService",
            deviceDataMonitorCalculationService);
    }

    DeviceDataMonitor setupMonitor(BuiltInAttribute... processorAttributes) {
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        Lists.newArrayList(processorAttributes).forEach(attribute -> {
            processors.add(getProcessor(attribute, STATE_GROUP_1, STATE_1));
        });
        DeviceDataMonitor monitor = new DeviceDataMonitor(1, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_1.testName,
            MONITORING_GROUP_1, true, processors);
        monitor.setViolationGroup(VIOLATIONS_GROUP_1);

        return monitor;
    }

    DeviceDataMonitor setupMonitor(List<DeviceDataMonitorProcessor> processors) {
        DeviceDataMonitor monitor = new DeviceDataMonitor(1, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_1.testName,
            MONITORING_GROUP_1, true, processors);
        monitor.setViolationGroup(VIOLATIONS_GROUP_1);

        return monitor;
    }

    @Test
    public void should_add_pao_to_violations_group_when_match_one() {
        RichPointData pointData = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        DeviceDataMonitor monitor = setupMonitor(BuiltInAttribute.OUTAGE_STATUS, BuiltInAttribute.BLINK_COUNT);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);
    }

    @Test
    public void should_add_pao_to_violations_group_when_match_one_with_duplicate() {
        RichPointData pointData = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        DeviceDataMonitor monitor = setupMonitor(BuiltInAttribute.OUTAGE_STATUS, BuiltInAttribute.BLINK_COUNT);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);
    }

    @Test
    public void should_add_then_remove_pao_from_violations_group_when_match_one_then_not_match_one_on_same_point() {
        RichPointData pointData = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        DeviceDataMonitor monitor = setupMonitor(BuiltInAttribute.OUTAGE_STATUS, BuiltInAttribute.BLINK_COUNT);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);

        pointDatas.clear();
        pointData = getRPD(1, 2.0, PAO_POINT_IDENTIFIER_1);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
    }

    @Test
    public void should_not_add_pao_to_violations_group_when_no_match() {
        RichPointData pointData = getRPD(1, 2.0, PAO_POINT_IDENTIFIER_1);
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        DeviceDataMonitor monitor = setupMonitor(BuiltInAttribute.OUTAGE_STATUS, BuiltInAttribute.BLINK_COUNT);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
    }

    @Test
    public void should_only_add_one_to_violations_group_when_match_one_then_not_match_another_bc_not_in_monitoring_group() {
        RichPointData pointData = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        DeviceDataMonitor monitor = setupMonitor(BuiltInAttribute.OUTAGE_STATUS, BuiltInAttribute.BLINK_COUNT);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, pointData);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);
        pointDatas.clear();
        // not added to violation group because not in monitoring group
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, getRPD(2, 2.0, PAO_POINT_IDENTIFIER_2));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);
    }

    @Test
    public void should_add_two_to_violations_group_when_match_two() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        DeviceDataMonitor monitor = setupMonitor(BuiltInAttribute.OUTAGE_STATUS, BuiltInAttribute.BLINK_COUNT);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_2.getPaoIdentifier());

        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 1);
        pointDatas.clear();
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, getRPD(2, 1.0, PAO_POINT_IDENTIFIER_2));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 2);
    }

    /*
     * matching matrix (a=attribute, g=state group, s=state)
     * 
     * should only add the Point's PAO to our violations group when all 3 match
     * 
     *   a | g | s
     * _____________
     * |   |   |   |
     * |___|___|___|
     * | x |   |   |
     * |___|___|___|
     * |   | x |   |
     * |___|___|___|
     * |   |   | x |
     * |___|___|___|
     * | x | x |   |
     * |___|___|___|
     * | x |   | x |
     * |___|___|___|
     * |   | x | x |
     * |___|___|___|
     * | x | x | x |
     * |___|___|___|
     */

    /*
     *   a | g | s
     * _____________
     * |   |   |   |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_none_match() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;

        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.CLOCK_ERROR, STATE_GROUP_2, STATE_2);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 5.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
    }

    /*
     *   a | g | s
     * _____________
     * | x |   |   |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_attribute_matches() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;

        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_2, STATE_2);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertEquals(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
    }

    /*
     *   a | g | s
     * _____________
     * |   | x |   |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_stategroup_matches() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor =
            getProcessor(BuiltInAttribute.CONFIGURATION_ERROR, STATE_GROUP_2, STATE_2);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    /*
     *   a | g | s
     * _____________
     * |   |   | x |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_state_matches() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor =
            getProcessor(BuiltInAttribute.CONFIGURATION_ERROR, STATE_GROUP_1, STATE_2);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    /*
     *   a | g | s
     * _____________
     * | x | x |   |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_attribute_and_stategroup_matches() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_1, STATE_2);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);

        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        Assert.assertEquals(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
    }

    /*
     *   a | g | s
     * _____________
     * | x |   | x |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_attribute_and_state_matches() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_2, STATE_1);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 2.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertEquals(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    /*
     *   a | g | s
     * _____________
     * |   | x | x |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_stategroup_and_state_matches() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.CLOCK_ERROR, STATE_GROUP_1, STATE_1);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }


    /*
     * a | g | s
     * _____________
     * | x | x | x |
     * |___|___|___|
     */

    @Test
    public void should_add_pao_when_all_match() {
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_1, STATE_1);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);

        Assert.assertEquals(processor.getAttribute(), attribute);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }

    @Test
    public void value_based_less_in_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor =
            getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, ProcessorType.LESS, 100.00);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }

    @Test
    public void value_based_less_out_of_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor =
            getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, ProcessorType.LESS, 100.00);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        deviceGroupMemberEditorDao.addDevices(monitor.getViolationGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 200, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(monitor.getViolationGroup()), 0);
    }

    @Test
    public void value_based_greater_in_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor =
            getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, ProcessorType.GREATER, 100.00);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 200.0, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }

    @Test
    public void value_based_greater_out_of_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor =
            getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, ProcessorType.GREATER, 100.00);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        deviceGroupMemberEditorDao.addDevices(monitor.getViolationGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    @Test
    public void value_based_range_in_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, 100.00, 1000.00);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 101, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }

    @Test
    public void value_based_range_out_of_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, 100.00, 1000.00);
        processors.add(processor);
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());
        deviceGroupMemberEditorDao.addDevices(monitor.getViolationGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 99, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    @Test
    public void value_based_and_state_based_in_violation() {
        attribute = BuiltInAttribute.DELIVERED_KWH;
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        processors.add(getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_1, STATE_1));
        processors.add(getValueBasedProcessor(BuiltInAttribute.DELIVERED_KWH, 100.00, 1000.00));
        DeviceDataMonitor monitor = setupMonitor(processors);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) monitor.getGroup(),
            PAO_POINT_IDENTIFIER_1.getPaoIdentifier());

        RichPointData rpd = getRPD(1, 1001, PAO_POINT_IDENTIFIER_5);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    private DeviceDataMonitorProcessor getValueBasedProcessor(BuiltInAttribute attribute, ProcessorType type,
            Double value) {
        DeviceDataMonitorProcessor ddmp = new DeviceDataMonitorProcessor(null, type, null, attribute);
        ddmp.setProcessorValue(value);
        return ddmp;
    }

    private DeviceDataMonitorProcessor getValueBasedProcessor(BuiltInAttribute attribute, Double min, Double max) {
        DeviceDataMonitorProcessor ddmp = new DeviceDataMonitorProcessor(null, ProcessorType.RANGE, null, attribute);
        ddmp.setRangeMin(min);
        ddmp.setRangeMax(max);
        return ddmp;
    }

    private DeviceDataMonitorProcessor getProcessor(BuiltInAttribute attribute, LiteStateGroup stateGroup,
            LiteState state) {
        DeviceDataMonitorProcessor ddmp = new DeviceDataMonitorProcessor(null, ProcessorType.STATE, null, attribute);
        ddmp.setState(state);
        ddmp.setStateGroup(stateGroup);
        return ddmp;
    }

    private RichPointData getRPD(int id, double value, PaoPointIdentifier paoPointIdentifier) {
        PointValue pointValue = new PointValue(id, value, paoPointIdentifier.getPointIdentifier().getPointType());
        RichPointData richPointData = new RichPointData(pointValue, paoPointIdentifier);
        pointDatas.add(richPointData);
        return richPointData;
    }

    private class DeviceGroupTest extends StoredDeviceGroup {
        public String testName;

        public DeviceGroupTest(String testName) {
            this.testName = testName;
            setName(testName);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((testName == null) ? 0 : testName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            DeviceGroupTest other = (DeviceGroupTest) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (testName == null) {
                if (other.testName != null) {
                    return false;
                }
            } else if (!testName.equals(other.testName)) {
                return false;
            }
            return true;
        }

        private DeviceDataMonitorTest getOuterType() {
            return DeviceDataMonitorTest.this;
        }
    }

    private class PointValue extends PointData {
        public PointValue(int id, double value, PointType type) {
            super();
            setId(id);
            setValue(value);
            setType(type.getPointTypeId());
            setPointQuality(PointQuality.Normal);
        }
    }
}
