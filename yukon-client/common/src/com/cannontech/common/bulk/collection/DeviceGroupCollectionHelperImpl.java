package com.cannontech.common.bulk.collection;

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

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
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
        
        return new ListBasedDeviceCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType());
                paramMap.put(getParameterName("name"), group.getFullName());

                return paramMap;
            }

            public List<YukonDevice> getDeviceList() {
                List<YukonDevice> deviceList = new ArrayList<YukonDevice>();

                Set<YukonDevice> devices = deviceGroupService.getDevices(Collections.singletonList(group));
                deviceList.addAll(devices);

                return deviceList;
            }
            
            @Override
            public long getDeviceCount() {
                return deviceGroupService.getDeviceCount(Collections.singletonList(group));
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group", group.getFullName());
            }

        };
    }
    
    @Transactional
    public DeviceCollection createDeviceGroupCollection(Iterator<Integer> deviceIds) {
        
        // step 1, create a new group with random name (will delete itself in 24 hours)
        final StoredDeviceGroup group = temporaryDeviceGroupService.createTempGroup(null);
        
        // step 2, add new devices
        deviceGroupMemberEditorDao.addDevicesById(group, deviceIds);
        
        // step 3, build DeviceCollection
        DeviceCollection deviceCollection = buildDeviceCollection(group);
        
        return deviceCollection;
    }

}
