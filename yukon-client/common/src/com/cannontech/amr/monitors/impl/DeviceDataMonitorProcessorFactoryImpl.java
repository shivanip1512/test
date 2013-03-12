package com.cannontech.amr.monitors.impl;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.DeviceDataMonitorCacheService;
import com.cannontech.amr.monitors.DeviceDataMonitorProcessorFactory;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;

public class DeviceDataMonitorProcessorFactoryImpl extends MonitorProcessorFactoryBase<DeviceDataMonitor> implements DeviceDataMonitorProcessorFactory {

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceDataMonitorCacheService deviceDataMonitorCacheService;
    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorProcessorFactoryImpl.class);

    @Override
    protected List<DeviceDataMonitor> getAllMonitors() {
        return deviceDataMonitorCacheService.getAllEnabledMonitors();
    }

    @Override
    protected RichPointDataListener createPointListener(final DeviceDataMonitor monitor) {
        return new RichPointDataListener() {
            @Override
            public void pointDataReceived(RichPointData richPointData) {
                handlePointDataReceived(monitor, richPointData);
            }
        };
    }

    @Override
    public void handlePointDataReceived(final DeviceDataMonitor monitor, RichPointData richPointData) {
        /* check if this point has a quality we should deem as acceptable to continue on with analysis */
        PointQuality pq = richPointData.getPointValue().getPointQuality();
        if (pq == PointQuality.Invalid ||
            pq == PointQuality.Uninitialized ||
            pq == PointQuality.Unknown) {
            return;
        }
        
        /* check if this point is of type status and who's Pao is of category Device */
        if (!isPointStatusAndBelongsToDevice(richPointData.getPaoPointIdentifier())) return;
        
        DeviceGroup groupToMonitor = deviceGroupService.findGroupName(monitor.getGroupName());
        if (groupToMonitor == null) {
            // group does not exist, have nothing to monitor
            return;
        }
        
        PaoIdentifier paoIdentifier = richPointData.getPaoPointIdentifier().getPaoIdentifier();
        SimpleDevice simpleDevice = new SimpleDevice(paoIdentifier);
        boolean deviceInGroup = deviceGroupService.isDeviceInGroup(groupToMonitor, simpleDevice);
        if (!deviceInGroup) {
            // if this device isn't in the group we're monitoring
            LogHelper.debug(log, "device [%s] not in monitoring group [%s]", simpleDevice, groupToMonitor);
            return;
        }
        
        PointValueHolder pointValueHolder = richPointData.getPointValue();
        LitePoint litePoint = pointDao.getLitePoint(pointValueHolder.getId());
        
        LogHelper.debug(log, "Point %s caught by Device Data Monitor: %s with value: %s",
                        richPointData.getPaoPointIdentifier(),
                        monitor,
                        pointValueHolder);
        
        Boolean matchFound = null;
        for (DeviceDataMonitorProcessor processor : monitor.getProcessors()) {
            // check attribute
            if (!attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), processor.getAttribute())) {
                continue;
            }
            
            // check state group
            if (!(litePoint.getStateGroupID() == processor.getStateGroup().getStateGroupID())) {
                continue;
            }
            
            /* only set matchFound to true if it isn't already true and the pointValue is a match */
            if (deviceDataMonitorService.isPointValueMatch(processor, pointValueHolder)) {
                matchFound = true;
            } else if (matchFound == null) {
                matchFound = false;
            }
        }
        if (matchFound != null) {
            StoredDeviceGroup violationsDeviceGroup = deviceGroupEditorDao.getStoredGroup(monitor.getViolationsDeviceGroupPath(), true);
            boolean inViolationsGroup = deviceGroupService.isDeviceInGroup(violationsDeviceGroup, paoIdentifier);
            if (matchFound) {
                /*
                 * we've got a match! add this device to the monitor violations group
                 * (if its NOT already there)
                 */
                if (!inViolationsGroup) {
                    deviceGroupMemberEditorDao.addDevices(violationsDeviceGroup, paoIdentifier);
                    LogHelper.debug(log, "Device Data Monitor [%s] found a data match! Adding device to device group [%s]",
                                    monitor.getName(),
                                    monitor.getViolationsDeviceGroupPath());
                } else {
                    LogHelper.debug(log, "Device Data Monitor [%s] found a data match! Doing nothing since the device is already in the violations group",
                                    monitor.getName());
                }
            } else {
                /*
                 * not a match! remove this device from the monitor violations group
                 * (if it IS already in there)
                 */
                if (inViolationsGroup) {
                    deviceGroupMemberEditorDao.removeDevicesById(violationsDeviceGroup, Collections.singleton(paoIdentifier.getPaoId()));
                    LogHelper.debug(log, "Device Data Monitor [%s] data did NOT match! Removing device from device group [%s]",
                                    monitor.getName(),
                                    monitor.getViolationsDeviceGroupPath());
                } else {
                    LogHelper.debug(log, "Device Data Monitor [%s] data did NOT match! Doing nothing since we weren't in the violations group",
                                    monitor.getName());
                }
            }
        }
    }

    /**
     * check if this point is of type status and who's Pao is of category Device
     */
    private boolean isPointStatusAndBelongsToDevice(PaoPointIdentifier paoPointIdentifier) {
        PaoIdentifier paoIdentifier = paoPointIdentifier.getPaoIdentifier();
        if (paoIdentifier.getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
            // non devices can't be in groups
            return false;
        }

        // check to make sure this point is a status point
        PointType pointType = paoPointIdentifier.getPointIdentifier().getPointType();
        if (!pointType.isStatus()) {
            return false;
        }
        
        if (paoIdentifier.getPaoType() == PaoType.SYSTEM) {
            // in testing I found a lot of cases where this factory would catch a point with
            // deviceId of 0 and type of SYSTEM. Not sure what this is... but denying it here.
            return false;
        }
        
        return true;
    }
}