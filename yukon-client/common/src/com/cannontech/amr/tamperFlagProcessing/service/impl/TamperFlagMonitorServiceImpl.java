package com.cannontech.amr.tamperFlagProcessing.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;

public class TamperFlagMonitorServiceImpl implements TamperFlagMonitorService {

	private DeviceGroupEditorDao deviceGroupEditorDao;
	private TamperFlagMonitorDao tamperFlagMonitorDao;
	
	@Override
	public StoredDeviceGroup getTamperFlagGroup(String name) {

		String tameprFlagGroupName = SystemGroupEnum.TAMPER_FLAG_PROCESSING.getFullPath() + name;
		StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(tameprFlagGroupName, true);
		
		return outageGroup;
	}
	
	@Override
	public boolean deleteTamperFlagMonitor(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException {

		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
        
        // delete tamper flag group
        try {
        	String tamperFlagGroupName = SystemGroupEnum.TAMPER_FLAG_PROCESSING.getFullPath() + tamperFlagMonitor.getTamperFlagMonitorName();
			StoredDeviceGroup tamperFlagGroup = deviceGroupEditorDao.getStoredGroup(tamperFlagGroupName, false);
			deviceGroupEditorDao.removeGroup(tamperFlagGroup);
		} catch (NotFoundException e) {
			// may have been deleted? who cares
		}
        
        // delete monitor
        boolean deleteOk = tamperFlagMonitorDao.delete(tamperFlagMonitorId) == 1;
        
        return deleteOk;
	}
	
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}

	@Autowired
	public void setTamperFlagMonitorDao(TamperFlagMonitorDao tamperFlagMonitorDao) {
		this.tamperFlagMonitorDao = tamperFlagMonitorDao;
	}
}
