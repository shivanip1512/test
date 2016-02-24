package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionById;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.db.device.Device;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.util.ServletUtil;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Implementation of DeviceCollectionProducer for an id list
 */
public class DeviceIdListCollectionProducer implements DeviceCollectionProducer {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired @Qualifier("memory") private DeviceMemoryCollectionProducer memoryCollectionProducer;
    
    public static final int PARTITION_SIZE = 1000;
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.idList;
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase collectionBase) {
        DeviceCollectionType collectionType = collectionBase.getCollectionType();
        DeviceCollectionDbType collectionDbType = collectionBase.getCollectionDbType();
        if(collectionType != DeviceCollectionType.idList || collectionDbType != DeviceCollectionDbType.DEVICE_LIST) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                + collectionType + ", Persistence type: " + collectionDbType);
        }
        DeviceCollectionById collectionById = (DeviceCollectionById) collectionBase;
        return createDeviceCollection(collectionById.getDeviceIds(), null);
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.idList) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        return DeviceCollectionById.create(DeviceCollectionType.idList, deviceCollection.getDeviceList());
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request) throws ServletRequestBindingException {
        final String ids = ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName("ids"));
        final List<Integer> idList = ServletUtil.getIntegerListFromString(ids);
        
        boolean containsSystemDevice = Iterables.any(idList, Predicates.equalTo(Device.SYSTEM_DEVICE_ID));
        Validate.isTrue(!containsSystemDevice, "cannot create DeviceCollection that contains the system device");
        
        for (List<Integer> devicesId : Lists.partition(idList, PARTITION_SIZE)) {
            if (devicesId.size() > 200){
            /* For large lists of ids, convert to memory list since url's can only be so long. */
            memoryCollectionProducer.createDeviceCollection(devicesId);
            }
        }
        return createDeviceCollection(idList, ids);
    }
    
    public DeviceCollection createDeviceCollection(final List<Integer> deviceIds, String optionalIdsString) {
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
            public int getDeviceCount() {
                return deviceIds.size();
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.idList");
            }
        };
    }

}