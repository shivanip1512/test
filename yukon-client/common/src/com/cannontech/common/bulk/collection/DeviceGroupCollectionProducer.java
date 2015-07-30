package com.cannontech.common.bulk.collection;

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
 * Implementation of DeviceCollection for a device group
 */
public class DeviceGroupCollectionProducer implements DeviceCollectionProducer {
    
    @Autowired private DeviceGroupService deviceGroupService = null;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.group;
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {
        
        final String groupParameterName = getSupportedType().getParameterName(DeviceGroupCollectionHelper.NAME_PARAM_NAME);
        final String groupName = ServletRequestUtils.getStringParameter(request, groupParameterName);
        final String descriptionParameterName = getSupportedType().getParameterName(DeviceGroupCollectionHelper.DESCRIPTION_PARAM_NAME);
        final String description = ServletRequestUtils.getStringParameter(request, descriptionParameterName);
        final DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        return deviceGroupCollectionHelper.buildDeviceCollection(group, description, null, null);
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if (type != DeviceCollectionType.group) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        return deviceGroupCollectionHelper.buildDeviceCollectionBase(deviceCollection);
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase base) {
        
        DeviceCollectionType type = base.getCollectionType();
        DeviceCollectionDbType dbType = base.getCollectionDbType();
        
        if (type != DeviceCollectionType.group || dbType != DeviceCollectionDbType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                + type + ", Persistence type: " + dbType);
        }
        
        Set<DeviceCollectionField> fields = ((DeviceCollectionByField) base).getFields();
        String description = null;
        String groupName = null;
        for (DeviceCollectionField field : fields) {
            if (field.getName().equalsIgnoreCase(DeviceGroupCollectionHelper.DESCRIPTION_PARAM_NAME)) {
                description = field.getValue();
            } else if (field.getName().equalsIgnoreCase(DeviceGroupCollectionHelper.NAME_PARAM_NAME)) {
                groupName = field.getValue();
            }
        }
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        return deviceGroupCollectionHelper.buildDeviceCollection(group, description, null, null);
    }
    
}