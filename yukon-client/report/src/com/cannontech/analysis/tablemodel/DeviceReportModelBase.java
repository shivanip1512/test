package com.cannontech.analysis.tablemodel;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.core.dao.DeviceDao;
import com.google.common.collect.Lists;

public abstract class DeviceReportModelBase<T> extends BareDatedReportModelBase<T> {

    private Logger log = YukonLogManager.getLogger(DeviceReportModelBase.class);
    
    private List<String> groupsFilter;
    private List<String> deviceFilter;

    private DeviceGroupService deviceGroupService;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceDao deviceDao;
    
	protected Iterable<SimpleDevice> getDeviceList() {
		if (!IterableUtils.isEmpty(groupsFilter)) {
            Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupsFilter);
            return deviceGroupService.getDevices(groups);
        } else if (!IterableUtils.isEmpty(deviceFilter)) {
            List<SimpleDevice> devices = Lists.newArrayList();
            for(String deviceName : deviceFilter){
                try {
                    devices.add(deviceDao.getYukonDeviceObjectByName(deviceName));
                } catch (DataAccessException e) {
                    log.error("Unable to find device with name: " + deviceName + ". This device will be skipped.");
                    continue;
                }
            }
            return devices;
        } else {
            /* If they didn't pick anything to filter on, assume all devices. */
            /* Use contents of SystemGroupEnum.DEVICETYPES. */
            DeviceGroup group = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES);
            return deviceGroupService.getDevices(Collections.singletonList(group));
        }
    }
	
    public void setGroupsFilter(List<String> groupsFilter) {
		this.groupsFilter = groupsFilter;
	}
    
    public void setDeviceFilter(List<String> deviceFilter) {
		this.deviceFilter = deviceFilter;
	}
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
    
    @Autowired
    public void setDeviceGroupEditorDao(
			DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
    
}
