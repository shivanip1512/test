package com.cannontech.common.device.groups.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.util.predicate.Predicate;

public class DeviceGroupUiServiceImpl implements DeviceGroupUiService {

	private DeviceGroupProviderDao deviceGroupDao;
	
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
    
    @Autowired
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
		this.deviceGroupDao = deviceGroupDao;
	}
}
