package com.cannontech.amr.outageProcessing.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public class OutageMonitorServiceImpl implements OutageMonitorService {

	private OutageMonitorDao outageMonitorDao;
	private ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	
	public StoredDeviceGroup getOutageGroup(String name) {
		
		String outageGroupName = SystemGroupEnum.OUTAGE_PROCESSING.getFullPath() + name;
		StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(outageGroupName, true);
		
		return outageGroup;
	}
	
	
	public boolean deleteOutageMonitor(int outageMonitorId) throws OutageMonitorNotFoundException {
		
		// disable job
        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
        if (outageMonitor.getScheduledCommandJobId() > 0) {
        	scheduledGroupRequestExecutionService.disableJob(outageMonitor.getScheduledCommandJobId());
        }
        
        // delete outage group
        try {
        	String outageGroupName = SystemGroupEnum.OUTAGE_PROCESSING.getFullPath() + outageMonitor.getName();
			StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(outageGroupName, false);
			deviceGroupEditorDao.removeGroup(outageGroup);
		} catch (NotFoundException e) {
			// may have been deleted? who cares
		}
        
        // delete processor
        boolean deleteOk = outageMonitorDao.delete(outageMonitorId) == 1;
        
        return deleteOk;
	}
	
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionService(
			ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService) {
		this.scheduledGroupRequestExecutionService = scheduledGroupRequestExecutionService;
	}
	
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
}
