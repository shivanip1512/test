package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.db.device.Device;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.util.ServletUtil;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Implementation of DeviceCollectionProducer for an id list
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
    
    public DeviceCollection createDeviceCollection(HttpServletRequest request) throws ServletRequestBindingException {
        final String ids = ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName("ids"));
        final List<Integer> idList = ServletUtil.getIntegerListFromString(ids);
        
        boolean containsSystemDevice = Iterables.any(idList, Predicates.equalTo(Device.SYSTEM_DEVICE_ID));
        Validate.isTrue(!containsSystemDevice, "cannot create DeviceCollection that contains the system device");

        return new ListBasedDeviceCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("ids"), ids);

                return paramMap;
            }

            public List<SimpleDevice> getDeviceList() {

                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>();

                for (int id : idList) {
                    SimpleDevice device = deviceDao.getYukonDevice(id);
                    deviceList.add(device);
                }

                return deviceList;
            }
            
            @Override
            public long getDeviceCount() {
                return idList.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.idList");
            }
        };
    }
}
