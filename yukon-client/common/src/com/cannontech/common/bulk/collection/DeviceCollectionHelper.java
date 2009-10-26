package com.cannontech.common.bulk.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList;

public class DeviceCollectionHelper {
    public DeviceCollection createSingletonCollection(final YukonDevice yukonDevice) {
        return new ListBasedDeviceCollection() {
            private List<SimpleDevice> deviceList = ImmutableList.of(new SimpleDevice(yukonDevice));
            
            @Override
            public List<SimpleDevice> getDeviceList() {
                return deviceList;
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.idList");
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {
                Map<String, String> paramMap = new HashMap<String, String>();
                DeviceCollectionType type = DeviceCollectionType.idList;
                paramMap.put("collectionType", type.name());
                paramMap.put(type.getParameterName("ids"), Integer.toString(yukonDevice.getPaoIdentifier().getPaoId()));
                return paramMap;
            }
        };
    }
}
