package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistenceType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceListBasedCollectionPersistable;
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
    @Autowired private DeviceDao deviceDao;
    @Autowired @Qualifier("memory") private DeviceMemoryCollectionProducer memoryCollectionProducer;

    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.idList;
    }
    
    @Override
    public DeviceCollection getCollectionFromPersistable(DeviceCollectionPersistable persistable) {
        DeviceCollectionType collectionType = persistable.getCollectionType();
        DeviceCollectionPersistenceType persistenceType = persistable.getPersistenceType();
        if(collectionType != DeviceCollectionType.idList || persistenceType != DeviceCollectionPersistenceType.DEVICE_LIST) {
            throw new IllegalArgumentException("Unable to parse device collection persistable. Collection type: " 
                + collectionType + ", Persistence type: " + persistenceType);
        }
        DeviceListBasedCollectionPersistable listPersistable = (DeviceListBasedCollectionPersistable) persistable;
        return createDeviceCollection(listPersistable.getDeviceIds(), null);
    }
    
    @Override
    public DeviceCollectionPersistable getPersistableFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.idList) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        return DeviceListBasedCollectionPersistable.create(DeviceCollectionType.idList, deviceCollection.getDeviceList());
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request) throws ServletRequestBindingException {
        final String ids = ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName("ids"));
        final List<Integer> idList = ServletUtil.getIntegerListFromString(ids);

        boolean containsSystemDevice = Iterables.any(idList, Predicates.equalTo(Device.SYSTEM_DEVICE_ID));
        Validate.isTrue(!containsSystemDevice, "cannot create DeviceCollection that contains the system device");

        if (idList.size() > 200) {
            /* For large lists of ids, convert to memory list since url's can only be so long. */
            return memoryCollectionProducer.createDeviceCollection(idList);
        }
        return createDeviceCollection(idList, ids);
    }
    
    private DeviceCollection createDeviceCollection(final List<Integer> deviceIds, String optionalIdsString) {
        final String ids = optionalIdsString != null ? optionalIdsString : StringUtils.join(deviceIds, ",");
        
        return new ListBasedDeviceCollection() {
            @Override
            public DeviceCollectionType getCollectionType() {
                return getSupportedType();
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("ids"), ids);

                return paramMap;
            }

            @Override
            public List<SimpleDevice> getDeviceList() {

                List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>();

                for (int id : deviceIds) {
                    SimpleDevice device = deviceDao.getYukonDevice(id);
                    deviceList.add(device);
                }

                return deviceList;
            }

            @Override
            public long getDeviceCount() {
                return deviceIds.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.idList");
            }
        };
    }
}
