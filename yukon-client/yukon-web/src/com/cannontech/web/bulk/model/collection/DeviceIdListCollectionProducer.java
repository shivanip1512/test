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
import com.cannontech.common.bulk.collection.ListBasedDeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceIdListCollectionProducer extends DeviceCollectionProducerBase {


    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public String getSupportedType() {
        return "idList";
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        final String ids = ServletRequestUtils.getStringParameter(request,
                                                                  getParameterName("ids"));

        return new ListBasedDeviceCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType());
                paramMap.put(getParameterName("ids"), ids);

                return paramMap;
            }

            public List<YukonDevice> getDeviceList() {

                List<YukonDevice> deviceList = new ArrayList<YukonDevice>();

                String[] idStrings = ids.split(",");
                for (String id : idStrings) {
                    YukonDevice device = deviceDao.getYukonDevice(Integer.valueOf(id));
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
