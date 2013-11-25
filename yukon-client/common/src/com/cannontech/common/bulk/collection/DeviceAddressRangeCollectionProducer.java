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
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistenceType;
import com.cannontech.common.bulk.collection.device.persistable.FieldBasedCollectionPersistable;
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
    private static final String START = "start";
    private static final String END = "end";
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.addressRange;
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        int startAddress = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName(START), -1);
        int endAddress = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName(END), -1);
        
        Validate.isTrue(startAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(endAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(startAddress <= endAddress, "end address must be greater than start address");

        return createDeviceCollection(startAddress, endAddress);
    }
    
    @Override
    public DeviceCollection getCollectionFromPersistable(DeviceCollectionPersistable persistable) {
        DeviceCollectionType collectionType = persistable.getCollectionType();
        DeviceCollectionPersistenceType persistenceType = persistable.getPersistenceType();
        if(collectionType != DeviceCollectionType.addressRange || persistenceType != DeviceCollectionPersistenceType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection persistable. Collection type: " 
                    + collectionType + ", Persistence type: " + persistenceType);
        }
        FieldBasedCollectionPersistable fieldPersistable = (FieldBasedCollectionPersistable) persistable;
        int startAddress = Integer.parseInt(fieldPersistable.getValueMap().get(START));
        int endAddress = Integer.parseInt(fieldPersistable.getValueMap().get(END));
        
        return createDeviceCollection(startAddress, endAddress);
    }
    
    @Override
    public DeviceCollectionPersistable getPersistableFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.addressRange) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        String startAddressParameterName = getSupportedType().getParameterName(START);
        String startAddress = deviceCollection.getCollectionParameters().get(startAddressParameterName);
        String endAddressParameterName = getSupportedType().getParameterName(END);
        String endAddress = deviceCollection.getCollectionParameters().get(endAddressParameterName);
        Map<String, String> valueMap = Maps.newHashMap();
        valueMap.put(START, startAddress);
        valueMap.put(END, endAddress);
        
        return new FieldBasedCollectionPersistable(DeviceCollectionType.addressRange, valueMap);
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
                paramMap.put(getSupportedType().getParameterName(START),
                             String.valueOf(startAddress));
                paramMap.put(getSupportedType().getParameterName(END), String.valueOf(endAddress));

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
