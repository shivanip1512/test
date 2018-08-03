package com.cannontech.amr.deviceDataMonitor.service.impl;

import static com.cannontech.amr.deviceDataMonitor.model.ProcessorType.GREATER;
import static com.cannontech.amr.deviceDataMonitor.model.ProcessorType.LESS;
import static com.cannontech.amr.deviceDataMonitor.model.ProcessorType.OUTSIDE;
import static com.cannontech.amr.deviceDataMonitor.model.ProcessorType.RANGE;
import static com.cannontech.amr.deviceDataMonitor.model.ProcessorType.STATE;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.COMM_STATUS;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.DELIVERED_DEMAND;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.DISCONNECT_STATUS;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImpl;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Sets;

public class DeviceDataMonitorTest {

    private DeviceDataMonitorCalculationServiceImpl calcImpl = new  DeviceDataMonitorCalculationServiceImpl();
    private AttributeServiceImpl attrServiceImpl = new AttributeServiceImpl();
    private PaoDefinitionDaoImpl paoDefinitionImpl = new PaoDefinitionDaoImpl();
    private SimpleDevice DEVICE_1 = new SimpleDevice(1, PaoType.RFN420CD);
    private SimpleDevice DEVICE_2 = new SimpleDevice(2, PaoType.MCT420FD);
    private SimpleDevice DEVICE_3 = new SimpleDevice(3, PaoType.RFN410FD);
    private SimpleDevice DEVICE_4 = new SimpleDevice(4, PaoType.MCT420FL);
    private SimpleDevice DEVICE_5 = new SimpleDevice(5, PaoType.MCT420FL);
    private SimpleDevice DEVICE_6 = new SimpleDevice(6, PaoType.MCT410CL);
    private SimpleDevice DEVICE_7 = new SimpleDevice(7, PaoType.RFN410FX);
    
    @Before
    public void setUp() {
        ReflectionTestUtils.setField(attrServiceImpl, "paoDefinitionDao", paoDefinitionImpl);
        ReflectionTestUtils.setField(calcImpl, "attributeService", attrServiceImpl);
    }

    @Test
    public void test_shouldTheGroupBeModified() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Assert.assertTrue(ViolationHelper.shouldTheGroupBeModified(false, true));
        Assert.assertFalse(ViolationHelper.shouldTheGroupBeModified(true, false));
        Assert.assertNull(ViolationHelper.shouldTheGroupBeModified(true, true));
        Assert.assertNull(ViolationHelper.shouldTheGroupBeModified(false, false));
    }
    
    @Test
    public void test_findViolatingDevices() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        List<DeviceDataMonitorProcessor> processors = new ArrayList<>();
        processors.add(getProcessor(STATE, DISCONNECT_STATUS, 1));
        processors.add(getProcessor(STATE, COMM_STATUS, 2));
        DeviceDataMonitorProcessor processor = getProcessor(RANGE, DELIVERED_DEMAND, null);
        processor.setRangeMin(5.0);
        processor.setRangeMax(10.0);
        processors.add(processor);
        DeviceDataMonitor monitor = new DeviceDataMonitor(1, "test", null, null, true, processors);

        Map<Integer, PointValueQualityHolder> pointValues = new HashMap<>();
        // violating
        pointValues.put(1, getPointValue(PointType.Status, 1));
        // violating
        pointValues.put(2, getPointValue(PointType.Status, 1));
        // not violating - point value doesn't match
        pointValues.put(3, getPointValue(PointType.Status, 2));
        // not violating - state group value doesn't match
        pointValues.put(4, getPointValue(PointType.Status, 1));
        // violating
        pointValues.put(5, getPointValue(PointType.Status, 2));
        // violating
        pointValues.put(6, getPointValue(PointType.Analog, 6));
        // not violating - value out of range
        pointValues.put(7, getPointValue(PointType.Analog, 11));

        Map<Integer, Integer> pointIdsToStateGroup = new HashMap<>();
        pointIdsToStateGroup.put(1, 1);
        pointIdsToStateGroup.put(2, 1);
        pointIdsToStateGroup.put(3, 1);
        pointIdsToStateGroup.put(4, 2);
        pointIdsToStateGroup.put(5, 2);
        pointIdsToStateGroup.put(6, 0);
        pointIdsToStateGroup.put(7, 0);

        Map<BuiltInAttribute, Map<Integer, SimpleDevice>> attributeToPoints = new HashMap<>();
        
        Map<Integer, SimpleDevice> pointToDevice = new HashMap<>();
        pointToDevice.put(1, DEVICE_1);
        pointToDevice.put(2, DEVICE_2);
        pointToDevice.put(3, DEVICE_3);
        attributeToPoints.put(DISCONNECT_STATUS, pointToDevice);
        
        pointToDevice = new HashMap<>();
        pointToDevice.put(2, DEVICE_2);
        pointToDevice.put(4, DEVICE_4);
        pointToDevice.put(5, DEVICE_5);
        attributeToPoints.put(COMM_STATUS, pointToDevice);
        
        pointToDevice = new HashMap<>();
        pointToDevice.put(6, DEVICE_6);
        pointToDevice.put(7, DEVICE_7);
        attributeToPoints.put(DELIVERED_DEMAND, pointToDevice);
                
        Set<SimpleDevice> devices = ViolationHelper.findViolatingDevices(monitor, attributeToPoints, pointIdsToStateGroup, pointValues);
        
        Set<SimpleDevice> violating = Sets.newHashSet(DEVICE_1,DEVICE_2, DEVICE_5, DEVICE_6 );        
        Set<SimpleDevice> notViolating =  Sets.newHashSet(DEVICE_3, DEVICE_4, DEVICE_7);
        
        Assert.assertTrue(devices.equals(violating));
        Assert.assertTrue(Collections.disjoint(devices, notViolating));
    }
    
    @Test
    public void test_isViolating_with_multiple_processors_for_the_same_attribute() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // STATE
        List<DeviceDataMonitorProcessor> processors = new ArrayList<>();
        processors.add(getProcessor(STATE, DISCONNECT_STATUS, 1));
        processors.add(getProcessor(STATE, DISCONNECT_STATUS, 2));
        Integer state = 1;
        PointValueQualityHolder pointData = getPointValue(PointType.Status, 1);
        Assert.assertTrue(ViolationHelper.isViolating(processors, state, pointData));

        pointData = getPointValue(PointType.Status, 2);
        Assert.assertFalse(ViolationHelper.isViolating(processors, state, pointData));

        state = 2;
        pointData = getPointValue(PointType.Status, 2);
        Assert.assertTrue(ViolationHelper.isViolating(processors, state, pointData));
        
        //VALUE
        processors.clear();
        DeviceDataMonitorProcessor processor1 = getProcessor(GREATER, DELIVERED_DEMAND, null);
        processor1.setProcessorValue(10.0);
        DeviceDataMonitorProcessor processor2 = getProcessor(GREATER, DELIVERED_DEMAND, null);
        processor2.setProcessorValue(11.0);
        processors.add(processor1);
        processors.add(processor2);
        
        pointData = getPointValue(PointType.Analog, 11);
        Assert.assertTrue(ViolationHelper.isViolating(processors, null, pointData));
        
        pointData = getPointValue(PointType.Analog, 10);
        Assert.assertFalse(ViolationHelper.isViolating(processors, null, pointData));
    }

    @Test
    public void test_isViolating_each_processor_individually_with_boundary_values() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // STATE
        DeviceDataMonitorProcessor processor = getProcessor(STATE, DISCONNECT_STATUS, 1);
        Integer state = 1;
        PointValueQualityHolder pointData = getPointValue(PointType.Status, 1);
        Assert.assertTrue(ViolationHelper.isViolating(processor, state, pointData));

        pointData = getPointValue(PointType.Status, 2);
        Assert.assertFalse(ViolationHelper.isViolating(processor, state, pointData));

        // GREATER
        processor = getProcessor(GREATER, DELIVERED_DEMAND, null);
        processor.setProcessorValue(10.0);
        pointData = getPointValue(PointType.Analog, 11);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setProcessorValue(pointData.getValue() - 0.01);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));
        processor.setProcessorValue(pointData.getValue() + 0.01);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));

        processor.setProcessorValue(10.0);
        pointData = getPointValue(PointType.Analog, 9);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));

        // LESS
        processor = getProcessor(LESS, DELIVERED_DEMAND, null);
        processor.setProcessorValue(10.0);
        pointData = getPointValue(PointType.Analog, 9);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setProcessorValue(pointData.getValue() - 0.01);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));
        processor.setProcessorValue(pointData.getValue() + 0.01);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));

        processor.setProcessorValue(9.0);
        pointData = getPointValue(PointType.Analog, 10);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));

        // RANGE
        processor = getProcessor(RANGE, DELIVERED_DEMAND, null);
        processor.setRangeMin(5.0);
        processor.setRangeMax(10.0);
        pointData = getPointValue(PointType.Analog, 9);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setRangeMin(pointData.getValue() - 0.01);
        processor.setRangeMax(pointData.getValue() + 0.01);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setRangeMin(pointData.getValue());
        processor.setRangeMax(pointData.getValue() + 0.01);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setRangeMin(5.0);
        processor.setRangeMax(10.0);
        pointData = getPointValue(PointType.Analog, 11);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));

        // OUTSIDE
        processor = getProcessor(OUTSIDE, DELIVERED_DEMAND, null);
        processor.setRangeMin(5.0);
        processor.setRangeMax(10.0);
        pointData = getPointValue(PointType.Analog, 10);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));

        processor.setRangeMin(pointData.getValue() - 0.01);
        processor.setRangeMax(pointData.getValue() + 0.01);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setRangeMin(pointData.getValue());
        processor.setRangeMax(pointData.getValue() + 0.01);
        Assert.assertTrue(ViolationHelper.isViolating(processor, null, pointData));
        
        processor.setRangeMin(5.0);
        processor.setRangeMax(10.0);
        pointData = getPointValue(PointType.Analog, 9);
        Assert.assertFalse(ViolationHelper.isViolating(processor, null, pointData));
    }

    private PointValueQualityHolder getPointValue(PointType type, double value) {
        return new PointValueQualityHolder() {

            @Override
            public int getId() {
                return 0;
            }

            @Override
            public Date getPointDataTimeStamp() {
                return null;
            }

            @Override
            public int getType() {
                return type.getPointTypeId();
            }

            @Override
            public double getValue() {
                return value;
            }

            @Override
            public PointQuality getPointQuality() {
                return PointQuality.Normal;
            }

            @Override
            public PointType getPointType() {
                return type;
            }
        };
    }

    private DeviceDataMonitorProcessor getProcessor(ProcessorType type, BuiltInAttribute attribute, Integer state) {
        DeviceDataMonitorProcessor processor = new DeviceDataMonitorProcessor(1, type, 0, attribute);
        if (state != null) {
            processor.setState(new LiteState(state, null, 0, 0, 0));
            processor.setStateGroup(new LiteStateGroup(state));
        }
        return processor;
    }
}
