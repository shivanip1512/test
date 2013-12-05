package com.cannontech.common.bulk.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;

/**
 * Implementation of DeviceCollection for an address range
 */
public class DeviceGroupCollectionProducer implements DeviceCollectionProducer  {

    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;

    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Autowired
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

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

        return deviceGroupCollectionHelper.buildDeviceCollection(group, description);
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.group) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        return deviceGroupCollectionHelper.buildDeviceCollectionBase(deviceCollection);
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase collectionBase) {
        DeviceCollectionType collectionType = collectionBase.getCollectionType();
        DeviceCollectionDbType persistenceType = collectionBase.getCollectionDbType();
        if(collectionType != DeviceCollectionType.group || persistenceType != DeviceCollectionDbType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                + collectionType + ", Persistence type: " + persistenceType);
        }
        DeviceCollectionByField collectionByField = (DeviceCollectionByField) collectionBase;
        String description = collectionByField.getValueMap().get(DeviceGroupCollectionHelper.DESCRIPTION_PARAM_NAME);
        String groupName = collectionByField.getValueMap().get(DeviceGroupCollectionHelper.NAME_PARAM_NAME);
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        return deviceGroupCollectionHelper.buildDeviceCollection(group, description);
    }
}