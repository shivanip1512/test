package com.cannontech.common.bulk.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistenceType;
import com.cannontech.common.bulk.collection.device.persistable.FieldBasedCollectionPersistable;
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

        final String groupParameterName = getSupportedType().getParameterName(DeviceGroupCollectionHelper.NAME);
        final String groupName = ServletRequestUtils.getStringParameter(request, groupParameterName);
        final String descriptionParameterName = getSupportedType().getParameterName(DeviceGroupCollectionHelper.DESCRIPTION);
        final String description = ServletRequestUtils.getStringParameter(request, descriptionParameterName);
        final DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        return deviceGroupCollectionHelper.buildDeviceCollection(group, description);
    }
    
    @Override
    public DeviceCollectionPersistable getPersistableFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.group) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        return deviceGroupCollectionHelper.buildDeviceCollectionPersistable(deviceCollection);
    }
    
    @Override
    public DeviceCollection getCollectionFromPersistable(DeviceCollectionPersistable persistable) {
        DeviceCollectionType collectionType = persistable.getCollectionType();
        DeviceCollectionPersistenceType persistenceType = persistable.getPersistenceType();
        if(collectionType != DeviceCollectionType.group || persistenceType != DeviceCollectionPersistenceType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection persistable. Collection type: " 
                + collectionType + ", Persistence type: " + persistenceType);
        }
        FieldBasedCollectionPersistable fieldPersistable = (FieldBasedCollectionPersistable) persistable;
        String description = fieldPersistable.getValueMap().get(DeviceGroupCollectionHelper.DESCRIPTION);
        String groupName = fieldPersistable.getValueMap().get(DeviceGroupCollectionHelper.NAME);
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        return deviceGroupCollectionHelper.buildDeviceCollection(group, description);
    }
}