package com.cannontech.common.bulk.collection.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceGroupCollectionHelperImpl implements DeviceGroupCollectionHelper {
    private DeviceGroupService deviceGroupService;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(
            TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    public String getSupportedType() {
        return "group";
    }
    
    public String getParameterName(String shortName) {
        return getSupportedType() + "." + shortName;
    }
    
    public DeviceCollection buildDeviceCollection(final DeviceGroup group) {
        return buildDeviceCollection(group, null);
    }

    public DeviceCollection buildDeviceCollection(final DeviceGroup group, final String descriptionHint) {
        
        return new ListBasedDeviceCollection() {
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType());
                paramMap.put(getParameterName("name"), group.getFullName());
                if (org.apache.commons.lang.StringUtils.isNotBlank(descriptionHint)) {
                    paramMap.put(getParameterName("description"), descriptionHint);
                }

                return paramMap;
            }

            public List<SimpleDevice> getDeviceList() {
                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>();

                Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singletonList(group));
                deviceList.addAll(devices);

                return deviceList;
            }
            
            @Override
            public List<SimpleDevice> getDevices(int start, int size) {

                // more than we need so we can skip past start devices and retrieve size devices
                int retrieveCount = start + size;
                
                Set<SimpleDevice> deviceSet = deviceGroupService.getDevices(Collections.singletonList(group), retrieveCount);
                
                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>(deviceSet);
                 
                return deviceList.subList(start, Math.min(retrieveCount, deviceList.size()));
            }
            
            @Override
            public long getDeviceCount() {
                return deviceGroupService.getDeviceCount(Collections.singletonList(group));
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                if (group.isHidden()) {
                    if (descriptionHint != null) {
                        return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group.temporaryWithHint", descriptionHint);
                    } else {
                        return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group.temporary");
                    }
                } else {
                    return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group", group.getFullName());
                }
            }

        };
    }
    
    @Transactional
    public DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> devices, String descriptionHint) {
        
        // step 1, create a new group with random name (will delete itself in 24 hours)
        final StoredDeviceGroup group = temporaryDeviceGroupService.createTempGroup();
        
        // step 2, add new devices
        deviceGroupMemberEditorDao.addDevices(group, devices);
        
        // step 3, build DeviceCollection
        DeviceCollection deviceCollection = buildDeviceCollection(group, descriptionHint);
        
        return deviceCollection;
    }

}
