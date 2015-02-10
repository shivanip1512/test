package com.cannontech.common.bulk.collection;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionField;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;

/**
 * Implementation of DeviceCollection for a set of device groups
 */
public class DeviceGroupsCollectionProducer implements DeviceCollectionProducer {
    
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupCollectionHelper helper;
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.groups;
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest req)
            throws ServletRequestBindingException {
        
        DeviceCollectionType type = getSupportedType();
        final String name = type.getParameterName(DeviceGroupCollectionHelper.NAME_PARAM_NAME);
        final String[] groupNames = ServletRequestUtils.getStringParameters(req, name);
        Set<DeviceGroup> groups = new HashSet<>();
        for (String groupName : groupNames) {
            groups.add(deviceGroupService.resolveGroupName(groupName));
        }
        
        return helper.buildDeviceCollection(groups);
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection collection) {
        
        DeviceCollectionType type = collection.getCollectionType();
        if (type != DeviceCollectionType.groups) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        return helper.buildDeviceCollectionBase(collection);
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase base) {
        
        DeviceCollectionType type = base.getCollectionType();
        DeviceCollectionDbType dbType = base.getCollectionDbType();
        
        if (type != DeviceCollectionType.groups || dbType != DeviceCollectionDbType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                + type + ", Persistence type: " + dbType);
        }
        
        DeviceCollectionByField byField = (DeviceCollectionByField) base;
        Set<DeviceGroup> groups = new HashSet<>();
        
        for (DeviceCollectionField field : byField.getFields()) {
            if (field.getName().equalsIgnoreCase(DeviceGroupCollectionHelper.NAME_PARAM_NAME)) {
                groups.add(deviceGroupService.resolveGroupName(field.getValue()));
            }
        }
        
        return helper.buildDeviceCollection(groups);
    }
    
}