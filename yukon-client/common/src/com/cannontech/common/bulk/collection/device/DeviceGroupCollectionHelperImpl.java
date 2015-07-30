package com.cannontech.common.bulk.collection.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionField;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceGroupCollectionHelperImpl implements DeviceGroupCollectionHelper {
    
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private @Qualifier("memory") DeviceMemoryCollectionProducer memoryProducer;
    
    private final String key = "yukon.common.device.bulk.bulkAction.collection.group";
    
    public String getSupportedType() {
        return DeviceCollectionType.group.name();
    }
    
    public String getParameterName(String shortName) {
        return getSupportedType() + "." + shortName;
    }
    
    @Override
    public DeviceCollection buildDeviceCollection(final DeviceGroup group) {
        return buildDeviceCollection(group, null, null, null);
    }
    
    @Override
    public DeviceCollectionByField buildDeviceCollectionBase(DeviceCollection collection) {
        
        DeviceCollectionType type = collection.getCollectionType();
        if (type != DeviceCollectionType.group) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        Map<String, String> parameters = collection.getCollectionParameters();
        String name = parameters.get(getParameterName(NAME_PARAM_NAME));
        String description = parameters.get(getParameterName(DESCRIPTION_PARAM_NAME));
        
        if (description == null) description = "";
        
        Set<DeviceCollectionField> fields = new HashSet<>();
        fields.add(DeviceCollectionField.of(NAME_PARAM_NAME, name));
        fields.add(DeviceCollectionField.of(DESCRIPTION_PARAM_NAME, description));
        
        return new DeviceCollectionByField(DeviceCollectionType.group, fields);
    }
    
    @Override
    public DeviceCollection buildDeviceCollection(final DeviceGroup group, final String descriptionHint,
            Set<String> errorDevices, String header) {
    
        return new ListBasedDeviceCollection() {
            
            @Override
            public DeviceCollectionType getCollectionType() {
                return DeviceCollectionType.group;
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {
                
                Map<String, String> params = new HashMap<String, String>();
                
                params.put("collectionType", getSupportedType());
                params.put(getParameterName(NAME_PARAM_NAME), group.getFullName());
                if (StringUtils.isNotBlank(descriptionHint)) {
                    params.put(getParameterName(DESCRIPTION_PARAM_NAME), descriptionHint);
                }
                
                return params;
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
            public int getDeviceCount() {
                return deviceGroupService.getDeviceCount(Collections.singletonList(group));
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                
                if (group.isHidden()) {
                    if (descriptionHint != null) {
                        return new YukonMessageSourceResolvable(key + ".temporaryWithHint", descriptionHint);
                    }
                    return new YukonMessageSourceResolvable(key + ".temporary");
                }
                
                return new YukonMessageSourceResolvable(key, group.getFullName());
            }
            
            @Override
            public Set<String> getErrorDevices() {
                return errorDevices;
            }

            @Override
            public int getDeviceErrorCount() {
                if (errorDevices != null) {
                    return errorDevices.size();
                }
                return 0;
            }

            @Override
            public String getUploadFileName() {

                return descriptionHint;
            }

            @Override
            public String getHeader() {
                return header;
            }

        };
    }
    
    @Override
    @Transactional
    public DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> devices,
            String descriptionHint, Set<String> errorDevices, String header) {
        // step 1, create a new group with random name (will delete itself in 24 hours)
        final StoredDeviceGroup group = temporaryDeviceGroupService.createTempGroup();
        
        // step 2, add new devices
        deviceGroupMemberEditorDao.addDevices(group, devices);
        
        // step 3, build DeviceCollection
        DeviceCollection deviceCollection = buildDeviceCollection(group, descriptionHint, errorDevices, header);
        
        return deviceCollection;
    }

    @Override
    public DeviceCollection buildDeviceCollection(Set<DeviceGroup> groups) {
        return memoryProducer.createDeviceCollection(groups);
    }
    
}