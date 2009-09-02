package com.cannontech.core.monitors;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.impl.RichPointValue;

public class PointMonitorService implements PointDataListener, InitializingBean {

	private AsyncDynamicDataSource asyncDynamicDataSource;
	private DeviceGroupProviderDao deviceGroupProviderDao;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private PointDao pointDao;
	
	// we're going to have to figure out how to keep the following up-to-date
	// but accept the fact that occasionally points will be processed for out of date monitors
	private Set<PointMonitorProcessor> processors = new CopyOnWriteArraySet<PointMonitorProcessor>();
	

	@Override
	public void pointDataReceived(PointValueQualityHolder pointData) {

	    int pointId = pointData.getId();
	    PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);

	    RichPointValue richPointValue = new RichPointValue(pointData, paoPointIdentifier);
	    SimpleDevice simpleDevice = new SimpleDevice(paoPointIdentifier.getPaoIdentifier());

	    for (PointMonitorProcessor processor : getProcessors()) {
	        DeviceGroup groupToMonitor = processor.getGroupToMonitor();
	        boolean deviceInGroup = deviceGroupProviderDao.isDeviceInGroup(groupToMonitor, simpleDevice);
	        if (deviceInGroup) {
	            boolean matches = processor.evaluate(richPointValue);
	            if (matches) {
	                StoredDeviceGroup resultsGroup = processor.getResultsGroup();
	                deviceGroupMemberEditorDao.addDevices(resultsGroup, simpleDevice);
	            }
	        }
	    }
	}
	
	public Set<PointMonitorProcessor> getProcessors() {
        return processors;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO must change to the register for all point data method.
//		asyncDynamicDataSource.registerForPointData(this, null);
		
	}
	
	@Autowired
	public void setAsyncDynamicDataSource(
			AsyncDynamicDataSource asyncDynamicDataSource) {
		this.asyncDynamicDataSource = asyncDynamicDataSource;
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
	@Autowired
	public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
}
