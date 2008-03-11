package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.web.bulk.model.DeviceCollectionProducer;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceIdListCollectionProducer implements DeviceCollectionProducer {

    private String supportedType = "idList";

    private DeviceDao deviceDao = null;

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public String getSupportedType() {
        return supportedType;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        final String ids = ServletRequestUtils.getStringParameter(request,
                                                                  supportedType + ".ids");

        return new DeviceCollectionBase() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", supportedType);
                paramMap.put(supportedType + ".ids", ids);

                return paramMap;
            }

            protected List<YukonDevice> getDevices() {

                List<YukonDevice> deviceList = new ArrayList<YukonDevice>();

                String[] idStrings = ids.split(",");
                for (String id : idStrings) {
                    YukonDevice device = deviceDao.getYukonDevice(Integer.valueOf(id));
                    deviceList.add(device);
                }

                return deviceList;
            }

            public String getDescriptionKey() {
                return collectionKeyBase + "idList";
            }

            public List<String> getParameterList() {
                return Collections.emptyList();
            }

        };
    }
}
