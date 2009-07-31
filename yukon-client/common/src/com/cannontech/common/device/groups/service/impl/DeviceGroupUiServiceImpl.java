package com.cannontech.common.device.groups.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.service.PaoLoadingService;

public class DeviceGroupUiServiceImpl implements DeviceGroupUiService {

	private DeviceGroupProviderDao deviceGroupDao;
    private PaoLoadingService paoLoadingService;
    private DeviceGroupService deviceGroupService;
	
	public DeviceGroupHierarchy getDeviceGroupHierarchy(DeviceGroup root, Predicate<DeviceGroup> deviceGroupPredicate) {

        DeviceGroupHierarchy hierarchy = new DeviceGroupHierarchy();
        hierarchy.setGroup(root);

        setChildHierarchy(hierarchy, deviceGroupPredicate);

        return hierarchy;
    }
    
    public DeviceGroupHierarchy getFilteredDeviceGroupHierarchy(DeviceGroupHierarchy hierarchy, Predicate<DeviceGroup> deviceGroupPredicate) {
        
        return getDeviceGroupHierarchy(hierarchy.getGroup(), deviceGroupPredicate);
    }
    
    public List<DeviceGroup> getGroups(Predicate<DeviceGroup> deviceGroupPredicate) {
    	
    	List<DeviceGroup> allGroups = deviceGroupDao.getAllGroups();
    	
    	List<DeviceGroup> filteredGroups = new ArrayList<DeviceGroup>();
    	for (DeviceGroup group : allGroups) {
    		if (deviceGroupPredicate.evaluate(group)) {
    			filteredGroups.add(group);
    		}
    	}
    	
    	return filteredGroups;
    }
    
    /**
     * Helper method to recursively set child hierarchy
     * @param hierarchy - parent hierarchy to set children on
     */
    private void setChildHierarchy(DeviceGroupHierarchy hierarchy, Predicate<DeviceGroup> deviceGroupPredicate) {

        List<DeviceGroupHierarchy> childGroupList = new ArrayList<DeviceGroupHierarchy>();
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(hierarchy.getGroup());
        for (DeviceGroup childGroup : childGroups) {
            
            if (deviceGroupPredicate.evaluate(childGroup)) {
            
                DeviceGroupHierarchy childHierarchy = new DeviceGroupHierarchy();
                childHierarchy.setGroup(childGroup);
    
                setChildHierarchy(childHierarchy, deviceGroupPredicate);
    
                childGroupList.add(childHierarchy);
            }
        }

        hierarchy.setChildGroupList(childGroupList);
    }
    
    
    @Override
    public List<DisplayablePao> getChildDevicesByGroup(DeviceGroup group) {
        return getChildDevicesByGroup(group, Integer.MAX_VALUE);
    }

    @Override
    public List<DisplayablePao> getChildDevicesByGroup(
            DeviceGroup group, int maxRecordCount) {
        Set<SimpleDevice> childDevices = deviceGroupDao.getChildDevices(group, maxRecordCount);
        
        List<DisplayablePao> displayableDevices = paoLoadingService.getDisplayableDevices(childDevices);
        return displayableDevices;
    }

    @Override
    public List<DisplayablePao> getDevicesByGroup(DeviceGroup group) {
        return getDevicesByGroup(group, Integer.MAX_VALUE);
    }

    @Override
    public List<DisplayablePao> getDevicesByGroup(DeviceGroup group, int maxRecordCount) {
        
        Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singleton(group), maxRecordCount);
        
        List<DisplayablePao> result = paoLoadingService.getDisplayableDevices(devices);
        return result;
    }

    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
		this.deviceGroupDao = deviceGroupDao;
	}
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}
