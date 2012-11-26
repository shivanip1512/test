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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class DeviceGroupUiServiceImpl implements DeviceGroupUiService {

	private DeviceGroupProviderDao deviceGroupDao;
    private PaoLoadingService paoLoadingService;
    private DeviceGroupService deviceGroupService;
	
	public DeviceGroupHierarchy getDeviceGroupHierarchy(DeviceGroup root, Predicate<DeviceGroup> deviceGroupPredicate) {

        DeviceGroupHierarchy hierarchy = new DeviceGroupHierarchy();
        hierarchy.setGroup(root);
        
        List<DeviceGroup> allGroupsList = deviceGroupDao.getGroups(root);
        Multimap<DeviceGroup, DeviceGroup> childOfLookup = ArrayListMultimap.create(50, 50);
        for (DeviceGroup deviceGroup : allGroupsList) {
            childOfLookup.put(deviceGroup.getParent(), deviceGroup);
        }

        setChildHierarchy(hierarchy, root, childOfLookup, deviceGroupPredicate);

        return hierarchy;
    }
    
    @Override
    public DeviceGroupHierarchy getFilteredDeviceGroupHierarchy(DeviceGroupHierarchy hierarchy, Predicate<DeviceGroup> deviceGroupPredicate) {
        // recurse through children
        List<DeviceGroupHierarchy> childGroupList = Lists.newArrayListWithExpectedSize(hierarchy.getChildGroupList().size());
        DeviceGroupHierarchy tempResult = new DeviceGroupHierarchy();
        tempResult.setGroup(hierarchy.getGroup());
        tempResult.setChildGroupList(childGroupList);
        
        for (DeviceGroupHierarchy childHierarchy : hierarchy.getChildGroupList()) {
            if (deviceGroupPredicate.evaluate(childHierarchy.getGroup())) {
                DeviceGroupHierarchy filteredChildHierarchy = getFilteredDeviceGroupHierarchy(childHierarchy, deviceGroupPredicate);
                childGroupList.add(filteredChildHierarchy);
            }
        }
        
        return tempResult;
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
     * @param childOfLookup 
     * @param root 
     */
    private void setChildHierarchy(DeviceGroupHierarchy hierarchy, DeviceGroup root, Multimap<DeviceGroup, DeviceGroup> childOfLookup, Predicate<DeviceGroup> deviceGroupPredicate) {

        List<DeviceGroupHierarchy> childGroupList = new ArrayList<DeviceGroupHierarchy>();
        Iterable<DeviceGroup> childGroups = childOfLookup.get(root);
        for (DeviceGroup childGroup : childGroups) {
            
            if (deviceGroupPredicate.evaluate(childGroup)) {
            
                DeviceGroupHierarchy childHierarchy = new DeviceGroupHierarchy();
                childHierarchy.setGroup(childGroup);
    
                setChildHierarchy(childHierarchy, childGroup, childOfLookup, deviceGroupPredicate);
    
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
