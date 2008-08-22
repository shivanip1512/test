package com.cannontech.web.bulk.model.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;

/**
 * Implementation of DeviceCollection for an address range
 */
public class DeviceGroupCollectionProducer extends DeviceCollectionProducerBase  {
    
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
    
    public String getSupportedType() {
        return "group";
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        String groupParameterName = getParameterName("name");
        
        String groupName = ServletRequestUtils.getStringParameter(request, groupParameterName);
        
        String descriptionParameterName = getParameterName("description");
        
        String description = ServletRequestUtils.getStringParameter(request, descriptionParameterName);
        
        final DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        return deviceGroupCollectionHelper.buildDeviceCollection(group, description);
    }



}
