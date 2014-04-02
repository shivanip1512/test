package com.cannontech.common.bulk.collection.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Maps;

public class DeviceGroupCollectionHelperImpl implements DeviceGroupCollectionHelper {
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;

    public String getSupportedType() {
        return DeviceCollectionType.group.name();
    }
    
    public String getParameterName(String shortName) {
        return getSupportedType() + "." + shortName;
    }
    
    @Override
    public DeviceCollection buildDeviceCollection(final DeviceGroup group) {
        return buildDeviceCollection(group, null);
    }
    
    @Override
    public DeviceCollectionByField buildDeviceCollectionBase(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.group) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        Map<String, String> parameters = deviceCollection.getCollectionParameters();
        String name = parameters.get(getParameterName(NAME_PARAM_NAME));
        String description = parameters.get(getParameterName(DESCRIPTION_PARAM_NAME));
        if(description == null) {
            description = "";
        }
        Map<String, String> valueMap = Maps.newHashMap();
        valueMap.put(NAME_PARAM_NAME, name);
        valueMap.put(DESCRIPTION_PARAM_NAME, description);
        
        return new DeviceCollectionByField(DeviceCollectionType.group, valueMap);
    }
    
    @Override
    public DeviceCollection buildDeviceCollection(final DeviceGroup group, final String descriptionHint) {
        
        return new ListBasedDeviceCollection() {
            @Override
            public DeviceCollectionType getCollectionType() {
                return DeviceCollectionType.group;
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType());
                paramMap.put(getParameterName(NAME_PARAM_NAME), group.getFullName());
                if (StringUtils.isNotBlank(descriptionHint)) {
                    paramMap.put(getParameterName(DESCRIPTION_PARAM_NAME), descriptionHint);
                }

                return paramMap;
            }

            @Override
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
                    }
                    return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group.temporary");
                }
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.group", group.getFullName());
            }

        };
    }
    
    @Override
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
