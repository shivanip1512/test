package com.cannontech.common.bulk.collection;

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
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Maps;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceAddressRangeCollectionProducer implements DeviceCollectionProducer {
    @Autowired private PaoDao paoDao;
    private static final String startParamName = "start";
    private static final String endParamName = "end";
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.addressRange;
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        int startAddress = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName(startParamName), -1);
        int endAddress = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName(endParamName), -1);
        
        Validate.isTrue(startAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(endAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(startAddress <= endAddress, "end address must be greater than start address");

        return createDeviceCollection(startAddress, endAddress);
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase collectionBase) {
        DeviceCollectionType collectionType = collectionBase.getCollectionType();
        DeviceCollectionDbType collectionDbType = collectionBase.getCollectionDbType();
        if(collectionType != DeviceCollectionType.addressRange || collectionDbType != DeviceCollectionDbType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                    + collectionType + ", Persistence type: " + collectionDbType);
        }
        DeviceCollectionByField collectionByField = (DeviceCollectionByField) collectionBase;
        int startAddress = Integer.parseInt(collectionByField.getValueMap().get(startParamName));
        int endAddress = Integer.parseInt(collectionByField.getValueMap().get(endParamName));
        
        return createDeviceCollection(startAddress, endAddress);
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.addressRange) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        String startAddressParameterName = getSupportedType().getParameterName(startParamName);
        String startAddress = deviceCollection.getCollectionParameters().get(startAddressParameterName);
        String endAddressParameterName = getSupportedType().getParameterName(endParamName);
        String endAddress = deviceCollection.getCollectionParameters().get(endAddressParameterName);
        Map<String, String> valueMap = Maps.newHashMap();
        valueMap.put(startParamName, startAddress);
        valueMap.put(endParamName, endAddress);
        
        return new DeviceCollectionByField(DeviceCollectionType.addressRange, valueMap);
    }
    
    private DeviceCollection createDeviceCollection(final int startAddress, final int endAddress) {
        return new ListBasedDeviceCollection() {
            @Override
            public DeviceCollectionType getCollectionType() {
                return getSupportedType();
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName(startParamName),
                             String.valueOf(startAddress));
                paramMap.put(getSupportedType().getParameterName(endParamName), String.valueOf(endAddress));

                return paramMap;
            }

            @Override
            public List<SimpleDevice> getDeviceList() {

                List<PaoIdentifier> paoIdentifierList = paoDao.getPaosByAddressRange(startAddress,
                                                                                                 endAddress);
                List<SimpleDevice> deviceList = PaoUtils.asSimpleDeviceList(paoIdentifierList);
                return deviceList;
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.addressRange", startAddress, endAddress);
            }

        };
    }
}
