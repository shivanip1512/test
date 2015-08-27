package com.cannontech.services.deviceDataMonitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.deviceDataMonitor.service.impl.DeviceDataMonitorCalculationServiceImpl;
import com.cannontech.amr.deviceDataMonitor.service.impl.DeviceDataMonitorServiceImpl;
import com.cannontech.amr.monitors.impl.DeviceDataMonitorProcessorFactoryImpl;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.impl.DeviceGroupProviderDaoMain;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.impl.DeviceGroupEditorDaoImpl;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.impl.DeviceGroupServiceImpl;
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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.impl.PointDaoImpl;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Ignore("Todo fix me")
public class DeviceDataMonitorTest {
    
    /* resources */
    private DeviceDataMonitorProcessorFactoryImpl deviceDataMonitorProcessorFactory;
    private AttributeService attributeService;
    private DeviceDataMonitorService deviceDataMonitorService;
    private DeviceDataMonitorCalculationService deviceDataMonitorCalculationService;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupProviderDao deviceGroupDao;
    private PointDao pointDao;
    
    private final static LitePoint point1 = new LitePoint(1, "point1", 0, 0, 0, 1);
    private final static LitePoint point2 = new LitePoint(2, "point2", 0, 0, 0, 2);
    private final static LitePoint point3 = new LitePoint(3, "point3", 0, 0, 0, 3);
    private final static LitePoint point4 = new LitePoint(4, "point4", 0, 0, 0, 4);
    private final static Map<Integer, LitePoint> pointIdToLitePointMap = ImmutableMap.of(1, point1,
                                                                                         2, point2,
                                                                                         3, point3,
                                                                                         4, point4);
    private DeviceDataMonitor monitor1;
    private DeviceDataMonitor monitor1WithNameChanged;
    private DeviceDataMonitor monitor1Disabled;
    private DeviceDataMonitor monitor1WithGroupChanged;
    private DeviceDataMonitor monitor1WithProcessorsChanged;
    private DeviceDataMonitor monitor1WithNoProcessors;

    private DeviceDataMonitor monitor2;

    private final static LiteStateGroup STATE_GROUP_1 = new LiteStateGroup(1);
    private final static LiteStateGroup STATE_GROUP_2 = new LiteStateGroup(2);
    private final static LiteStateGroup STATE_GROUP_3 = new LiteStateGroup(3);
    
    private final static LiteState STATE_1 = new LiteState(1, null, 0, 0, 0);
    private final static LiteState STATE_2 = new LiteState(2, null, 0, 0, 0);
    private final static LiteState STATE_3 = new LiteState(3, null, 0, 0, 0);
    
    private final static PaoIdentifier PAO_IDENTIFIER_1 = new PaoIdentifier(1, PaoType.MCT410CL);
    private final static PaoIdentifier PAO_IDENTIFIER_2 = new PaoIdentifier(2, PaoType.MCT410CL);
    private final static PaoIdentifier PAO_IDENTIFIER_3 = new PaoIdentifier(3, PaoType.MCT410CL);
    
    private final static PointIdentifier POINT_IDENTIFIER_1 = new PointIdentifier(PointType.Status, 1);
    private final static PointIdentifier POINT_IDENTIFIER_2 = new PointIdentifier(PointType.Status, 2);
    private final static PointIdentifier POINT_IDENTIFIER_3 = new PointIdentifier(PointType.Status, 3);
    
    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER_1 = new PaoPointIdentifier(PAO_IDENTIFIER_1, POINT_IDENTIFIER_1);
    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER_2 = new PaoPointIdentifier(PAO_IDENTIFIER_2, POINT_IDENTIFIER_2);
    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER_3 = new PaoPointIdentifier(PAO_IDENTIFIER_3, POINT_IDENTIFIER_3);
    
    private final DeviceGroupTest MONITORING_GROUP_1 = new DeviceGroupTest("monitoring_1");
    private final DeviceGroupTest MONITORING_GROUP_2 = new DeviceGroupTest("monitoring_2");
    private final DeviceGroupTest MONITORING_GROUP_3 = new DeviceGroupTest("monitoring_3");

    private final DeviceGroupTest VIOLATIONS_GROUP_1 = new DeviceGroupTest("violations_1");
    private final DeviceGroupTest VIOLATIONS_GROUP_2 = new DeviceGroupTest("violations_2");
    private final DeviceGroupTest VIOLATIONS_GROUP_3 = new DeviceGroupTest("violations_3");
    
    private final Map<String, DeviceGroupTest> VIOLATIONS_GROUP_MAP = ImmutableMap
        .of(VIOLATIONS_GROUP_1.testName, VIOLATIONS_GROUP_1,
            VIOLATIONS_GROUP_2.testName, VIOLATIONS_GROUP_2,
            VIOLATIONS_GROUP_3.testName, VIOLATIONS_GROUP_3);
    
    private Map<DeviceGroup, HashSet<PaoIdentifier>> deviceGroupPaos;

    private Map<PaoPointIdentifier, ? extends Attribute> paoPointToAttributeMap = ImmutableMap
        .of(PAO_POINT_IDENTIFIER_1, BuiltInAttribute.OUTAGE_STATUS,
            PAO_POINT_IDENTIFIER_2, BuiltInAttribute.BLINK_COUNT,
            PAO_POINT_IDENTIFIER_3, BuiltInAttribute.CLOCK_ERROR);
    
    @Before
    public void setup() {
        attributeService = new AttributeServiceImpl() {
            @Override
            public boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute attribute) {
                return paoPointToAttributeMap.get(paoPointIdentifier).getKey().equals(attribute.getKey());
            }
        };
        deviceGroupService = new DeviceGroupServiceImpl() {
            @Override
            public DeviceGroup findGroupName(String groupName) {
                DeviceGroupTest deviceGroup = new DeviceGroupTest(groupName);
                return deviceGroup;
            }
            @Override
            public boolean isDeviceInGroup(DeviceGroup group, YukonPao pao) {
                HashSet<? extends YukonPao> paos = deviceGroupPaos.get(group);
                if (paos == null) {
                    return false;
                }
                return paos.contains(pao.getPaoIdentifier());
            }
        };
        deviceGroupEditorDao = new DeviceGroupEditorDaoImpl() {
            @Override
            public StoredDeviceGroup getStoredGroup(String fullName, boolean create) throws NotFoundException {
                int lastIndexOfSlash = fullName.lastIndexOf("/");
                String groupName = fullName.substring(lastIndexOfSlash+1);
                return VIOLATIONS_GROUP_MAP.get(groupName);
            }
        };
        deviceGroupMemberEditorDao = new DeviceGroupEditorDaoImpl() {
            @Override
            public int addDevices(StoredDeviceGroup group, YukonPao... pao) {
                List<YukonPao> paos = Lists.newArrayList(pao);
                HashSet<PaoIdentifier> paoIdentifiers = Sets.newHashSet(Lists.transform(paos, new Function<YukonPao, PaoIdentifier>() {
                    @Override
                    public PaoIdentifier apply(YukonPao yukonPao) {
                        return yukonPao.getPaoIdentifier();
                    }
                }));
                if (!deviceGroupPaos.containsKey(group)) {
                    deviceGroupPaos.put(group, paoIdentifiers);
                    return 1;
                } else {
                    deviceGroupPaos.get(group).addAll(paoIdentifiers);
                    return 0;
                }
            }
            @Override
            public int removeDevicesById(StoredDeviceGroup group, Collection<Integer> deviceIds) {
                int numAffected = 0;
                HashSet<PaoIdentifier> paoIds = deviceGroupPaos.get(group);
                for (YukonPao yukonPao : paoIds) {
                    if (deviceIds.contains(yukonPao.getPaoIdentifier().getPaoId())) {
                        deviceGroupPaos.get(group).remove(yukonPao.getPaoIdentifier());
                        numAffected++;
                    }
                }
                return numAffected;
            }
        };
        deviceGroupDao = new DeviceGroupProviderDaoMain() {
            @Override
            public int getDeviceCount(DeviceGroup group) {
                HashSet<PaoIdentifier> hashSet = deviceGroupPaos.get(group);
                if (hashSet == null) {
                    return 0;
                }
                return hashSet.size();
            }
        };
        pointDao = new PointDaoImpl() {
            @Override
            public LitePoint getLitePoint(int pointId) {
                return pointIdToLitePointMap.get(pointId);
            }
        };

        deviceDataMonitorCalculationService = new DeviceDataMonitorCalculationServiceImpl();
        deviceDataMonitorService = new DeviceDataMonitorServiceImpl();
        deviceDataMonitorProcessorFactory = new DeviceDataMonitorProcessorFactoryImpl();
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "attributeService", attributeService);
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "deviceDataMonitorService", deviceDataMonitorService);
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "deviceGroupEditorDao", deviceGroupEditorDao);
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "deviceGroupMemberEditorDao", deviceGroupMemberEditorDao);
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "deviceGroupService", deviceGroupService);
        ReflectionTestUtils.setField(deviceDataMonitorProcessorFactory, "pointDao", pointDao);
        
        deviceGroupPaos = Maps.newHashMap();
        deviceGroupPaos.put(MONITORING_GROUP_1, Sets.newHashSet(PAO_IDENTIFIER_1));
        deviceGroupPaos.put(MONITORING_GROUP_2, Sets.newHashSet(PAO_IDENTIFIER_1, PAO_IDENTIFIER_2));
        deviceGroupPaos.put(MONITORING_GROUP_3, Sets.newHashSet(PAO_IDENTIFIER_1, PAO_IDENTIFIER_2, PAO_IDENTIFIER_3));
        
        List<DeviceDataMonitorProcessor> processors1 = Lists.newArrayList();
        processors1.add(getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_1, STATE_1));
        processors1.add(getProcessor(BuiltInAttribute.BLINK_COUNT, STATE_GROUP_2, STATE_2));
        processors1.add(getProcessor(BuiltInAttribute.CLOCK_ERROR, STATE_GROUP_3, STATE_3));

        List<DeviceDataMonitorProcessor> processors2 = Lists.newArrayList(processors1);
        processors2.add(getProcessor(BuiltInAttribute.CONFIGURATION_ERROR, STATE_GROUP_3, STATE_3));
        
        monitor1                      = new DeviceDataMonitor(null, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_1.testName, true, processors1);
        monitor1Disabled              = new DeviceDataMonitor(null, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_1.testName, false, processors1);
        monitor1WithNameChanged       = new DeviceDataMonitor(null, VIOLATIONS_GROUP_2.testName, MONITORING_GROUP_1.testName, true, processors1);
        monitor1WithGroupChanged      = new DeviceDataMonitor(null, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_2.testName, true, processors1);
        monitor1WithProcessorsChanged = new DeviceDataMonitor(null, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_1.testName, true, processors2);
        monitor1WithNoProcessors      = new DeviceDataMonitor(null, VIOLATIONS_GROUP_1.testName, MONITORING_GROUP_1.testName, true, null);

        monitor2                      = new DeviceDataMonitor(null, VIOLATIONS_GROUP_2.testName, MONITORING_GROUP_2.testName, true, processors2);
    }

    @Test
    public void should_add_pao_to_violations_group_when_match_one() {
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }
    
    @Test
    public void should_add_pao_to_violations_group_when_match_one_with_duplicate() {
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }
    
    @Test
    public void should_add_then_remove_pao_from_violations_group_when_match_one_then_not_match_one_on_same_point() {
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 2.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }
    
    @Test
    public void should_not_add_pao_to_violations_group_when_no_match() {
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 2.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }

    @Test
    public void should_only_add_one_to_violations_group_when_match_one_then_not_match_another_bc_not_in_monitoring_group() {
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, getRPD(2, 2.0, PAO_POINT_IDENTIFIER_2));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }

    @Test
    public void should_add_two_to_violations_group_when_match_two() {
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_2), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor2, getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_2), 1);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor2, getRPD(2, 2.0, PAO_POINT_IDENTIFIER_2));
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_2), 2);
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
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.CLOCK_ERROR, STATE_GROUP_2, STATE_2);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 5.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertNotSame(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertFalse(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }
    
    /*
     *   a | g | s
     * _____________
     * | x |   |   |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_attribute_matches() {
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_2, STATE_2);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 5.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertEquals(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertNotSame(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertFalse(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));

        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }
    
    /*
     *   a | g | s
     * _____________
     * |   | x |   |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_stategroup_matches() {
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.CONFIGURATION_ERROR, STATE_GROUP_1, STATE_2);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 5.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertFalse(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
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
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.CONFIGURATION_ERROR, STATE_GROUP_2, STATE_1);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertNotSame(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertTrue(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
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
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_1, STATE_2);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 5.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertEquals(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertFalse(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }
    
    /*
     *   a | g | s
     * _____________
     * | x |   | x |
     * |___|___|___|
     */
    @Test
    public void should_not_add_pao_when_only_attribute_and_state_matches() {
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_2, STATE_1);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertEquals(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertNotSame(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertTrue(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
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
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.CLOCK_ERROR, STATE_GROUP_1, STATE_1);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertNotSame(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertTrue(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
    }
    
    /*
     *   a | g | s
     * _____________
     * | x | x | x |
     * |___|___|___|
     */
    @Test
    public void should_add_pao_when_all_match() {
        List<DeviceDataMonitorProcessor> processors = Lists.newArrayList();
        DeviceDataMonitorProcessor processor = getProcessor(BuiltInAttribute.OUTAGE_STATUS, STATE_GROUP_1, STATE_1);
        processors.add(processor);
        monitor1.setProcessors(processors);
        
        RichPointData rpd = getRPD(1, 1.0, PAO_POINT_IDENTIFIER_1);
        Assert.assertEquals(processor.getAttribute(), paoPointToAttributeMap.get(PAO_POINT_IDENTIFIER_1));
        Assert.assertEquals(processor.getStateGroup().getStateGroupID(), pointIdToLitePointMap.get(rpd.getPointValue().getId()).getStateGroupID());
        Assert.assertTrue(deviceDataMonitorCalculationService.isPointValueMatch(processor, rpd.getPointValue()));
        
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 0);
        deviceDataMonitorProcessorFactory.handlePointDataReceived(monitor1, rpd);
        Assert.assertEquals(deviceGroupDao.getDeviceCount(VIOLATIONS_GROUP_1), 1);
    }

    @Test
    public void should_not_update_violations_group_with_equal_monitors() {
        Assert.assertFalse(deviceDataMonitorCalculationService.shouldUpdateViolationsGroupNameBeforeSave(monitor1, monitor1));
    }
    
    @Test
    public void should_update_violations_group_with_monitors_with_diff_names() {
        Assert.assertTrue(deviceDataMonitorCalculationService.shouldUpdateViolationsGroupNameBeforeSave(monitor1, monitor1WithNameChanged));
    }
    
    @Test
    public void should_not_recalculate_violations_before_save_with_equal_monitors() {
        Assert.assertFalse(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1, monitor1));
    }
    
    @Test
    public void should_not_recalculate_violations_before_save_with_no_processors() {
        Assert.assertFalse(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1WithNoProcessors, monitor1));
    }
    
    @Test
    public void should_not_recalculate_violations_before_save_with_disabled_monitor() {
        Assert.assertFalse(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1Disabled, monitor1));
    }
    
    @Test
    public void should_recalculate_violations_before_save_with_null_old_monitor() {
        Assert.assertTrue(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1, null));
    }
    
    @Test
    public void should_recalculate_violations_before_save_with_changed_monitoring_group() {
        Assert.assertTrue(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1, monitor1WithGroupChanged));
    }
    
    @Test
    public void should_recalculate_violations_before_save_with_changed_processors() {
        Assert.assertTrue(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1, monitor1WithProcessorsChanged));
    }
    
    @Test
    public void should_recalculate_violations_before_save_with_enabling_existing_monitor() {
        Assert.assertTrue(deviceDataMonitorCalculationService.shouldFindViolatingPaosBeforeSave(monitor1, monitor1Disabled));
    }
    
    
    private DeviceDataMonitorProcessor getProcessor(Attribute attribute, LiteStateGroup stateGroup, LiteState state) {
        return new DeviceDataMonitorProcessor(null, null, attribute, stateGroup, state);
    }
    
    private RichPointData getRPD(int id, double value, PaoPointIdentifier paoPointIdentifier) {
        PointValue pointValue = new PointValue(id, value);
        RichPointData richPointData = new RichPointData(pointValue, paoPointIdentifier);
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
        public PointValue(int id, double value) {
            super();
            setId(id);
            setValue(value);
            setPointQuality(PointQuality.Normal);
        }
    }
}
