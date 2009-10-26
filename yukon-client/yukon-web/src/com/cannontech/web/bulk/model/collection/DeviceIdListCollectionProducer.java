package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceCollectionType;
import com.cannontech.common.bulk.collection.ListBasedDeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.bulk.model.DeviceCollectionProducer;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceIdListCollectionProducer implements DeviceCollectionProducer {


    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.idList;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        final String ids = ServletRequestUtils.getStringParameter(request,
                                                                  getSupportedType().getParameterName("ids"));

        return new ListBasedDeviceCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("ids"), ids);

                return paramMap;
            }

            public List<SimpleDevice> getDeviceList() {

                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>();

                String[] idStrings = ids.split(",");
                for (String id : idStrings) {
                    SimpleDevice device = deviceDao.getYukonDevice(Integer.valueOf(id));
                    deviceList.add(device);
                }

                return deviceList;
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.idList");
            }

        };
    }
}
