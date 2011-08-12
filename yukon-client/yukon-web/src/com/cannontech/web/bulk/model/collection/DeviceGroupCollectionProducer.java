package com.cannontech.web.bulk.model.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
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
    
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.group;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        String groupParameterName = getSupportedType().getParameterName("name");
        
        String groupName = ServletRequestUtils.getStringParameter(request, groupParameterName);
        
        String descriptionParameterName = getSupportedType().getParameterName("description");
        
        String description = ServletRequestUtils.getStringParameter(request, descriptionParameterName);
        
        final DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        return deviceGroupCollectionHelper.buildDeviceCollection(group, description);
    }



}
