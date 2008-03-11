package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.web.bulk.model.DeviceCollectionProducer;

/**
 * Implementation of DeviceCollection for an address range
 */
public class DeviceGroupCollectionProducer implements DeviceCollectionProducer {

    private String supportedType = "group";
    private DeviceGroupService deviceGroupService = null;

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    public String getSupportedType() {
        return supportedType;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        final String groupName = ServletRequestUtils.getStringParameter(request,
                                                                        supportedType + ".name");

        return new DeviceCollectionBase() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", supportedType);
                paramMap.put(supportedType + ".name", groupName);

                return paramMap;
            }

            protected List<YukonDevice> getDevices() {
                List<YukonDevice> deviceList = new ArrayList<YukonDevice>();

                DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
                Set<YukonDevice> devices = deviceGroupService.getDevices(Collections.singletonList(group));
                deviceList.addAll(devices);

                return deviceList;
            }
            
            public String getDescriptionKey() {
                return collectionKeyBase + "group";
            }
            
            public List<String> getParameterList() {

                List<String> paramList = new ArrayList<String>();
                paramList.add(groupName);

                return paramList;
            }

        };
    }

}
