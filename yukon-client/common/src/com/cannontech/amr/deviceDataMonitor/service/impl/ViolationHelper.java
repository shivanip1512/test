package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Range;

class ViolationHelper {

    static final Logger log = YukonLogManager.getLogger(ViolationHelper.class);

    /**
     * Returns true if device needs to be added to a group, returns false if it needs to be removed from the
     * group, returns null if no changes should be made to the group.
     * (unit test)
     */
    static Boolean shouldTheGroupBeModified(boolean inViolationsGroup, boolean foundViolation) {
        //found violation and device is not in violation group
        if(foundViolation && !inViolationsGroup) {
            return true;
        }
        
        // no violation found but device is in violation group
        if (!foundViolation && inViolationsGroup) {
            return false;
        }
        return null;
    }

    /**
     * Returns violating devices for monitor (unit test)
     */
    static Set<SimpleDevice> findViolatingDevices(DeviceDataMonitor monitor, Map<BuiltInAttribute, Map<Integer, SimpleDevice>> attributeToPoints, Map<Integer, Integer> pointIdsToStateGroup, Map<Integer, PointValueQualityHolder> pointValues) {
        Set<SimpleDevice> violatingDevices = new HashSet<>();
        if (!pointValues.isEmpty()) {
            for (Entry<BuiltInAttribute, Map<Integer, SimpleDevice>> entry : attributeToPoints.entrySet()) {
                List<DeviceDataMonitorProcessor> processor = monitor.getProcessors(entry.getKey());
                for (Entry<Integer, SimpleDevice> pointToDevice : entry.getValue().entrySet()) {
                    SimpleDevice device = pointToDevice.getValue();
                    int pointId = pointToDevice.getKey();
                    int stateGroupId = pointIdsToStateGroup.get(pointId);
                    if (!violatingDevices.contains(device)) {
                        PointValueQualityHolder valuetHolder = pointValues.get(pointId);
                        if (isViolating(processor, stateGroupId, valuetHolder)) {
                            violatingDevices.add(device);
                        }
                    }
                }
            }
        }
        return violatingDevices;
    }

    /**
     * Returns true if violation found (unit test)
     * This method only works if 2 processors are of the same attribute
     */
    static boolean isViolating(List<DeviceDataMonitorProcessor> processors, Integer stateGroupId, PointValueQualityHolder pointValue) {
    
        if (pointValue != null && pointValue.getPointQuality().isInvalid()) {
            log.debug("Point Quality is invalid - Point value:{}.", pointValue);
            return false;
        }
        return processors.stream().anyMatch(processor -> isViolating(processor, stateGroupId, pointValue));
    }
    
    /**
     * Returns true if violation found (unit test)
     */
    static boolean isViolating(DeviceDataMonitorProcessor processor, Integer stateGroupId, PointValueQualityHolder pointValue) {
        PointType type = PointType.getForId(pointValue.getType());
        if (processor.getType() == ProcessorType.STATE && processor.getStateGroup().getStateGroupID() == stateGroupId
            && type.isStatus()) {
            return processor.getState().getStateRawState() == (int) pointValue.getValue();
        } else if (processor.getType() == ProcessorType.RANGE && type.isValuePoint()) {
            return Range.open(processor.getRangeMin(), processor.getRangeMax()).contains(pointValue.getValue());
        } else if (processor.getType() == ProcessorType.OUTSIDE && type.isValuePoint()) {
            return !Range.open(processor.getRangeMin(), processor.getRangeMax()).contains(pointValue.getValue());
        } else if (processor.getType() == ProcessorType.LESS && type.isValuePoint()) {
            return pointValue.getValue() < processor.getProcessorValue();
        } else if (processor.getType() == ProcessorType.GREATER && type.isValuePoint()) {
            return pointValue.getValue() > processor.getProcessorValue();
        }
        return false;
    }
}