package com.cannontech.core.monitors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;

public class PointMonitorListenerFactory {

	private DeviceGroupProviderDao deviceGroupProviderDao;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	
	public RichPointDataListener createListener(final PointMonitorProcessor processor) {
	    return new RichPointDataListener() {

	        @Override
	        public void pointDataReceived(RichPointData pointData) {
	            PaoIdentifier paoIdentifier = pointData.getPaoPointIdentifier().getPaoIdentifier();
                if (paoIdentifier.getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
	                // non devices can't be in groups
	                return;
	            }
	            SimpleDevice simpleDevice = new SimpleDevice(paoIdentifier);

	            DeviceGroup groupToMonitor = processor.getGroupToMonitor();
	            if (groupToMonitor != null) {	//this check supports a device group that is being monitored but no longer exists.
		            boolean deviceInGroup = deviceGroupProviderDao.isDeviceInGroup(groupToMonitor, simpleDevice);
		            if (deviceInGroup) {
		                boolean matches = processor.evaluate(pointData);
		                if (matches) {
		                    StoredDeviceGroup resultsGroup = processor.getResultsGroup();
		                    deviceGroupMemberEditorDao.addDevices(resultsGroup, simpleDevice);
		                }
		            }
	            }
	        }
	    };
	}

	@Autowired
	public void setDeviceGroupMemberEditorDao(
			DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}

	@Autowired
	public void setDeviceGroupProviderDao(
			DeviceGroupProviderDao deviceGroupProviderDao) {
		this.deviceGroupProviderDao = deviceGroupProviderDao;
	}
	
}
