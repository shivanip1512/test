package com.cannontech.common.bulk.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionField;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceAddressRangeCollectionProducer implements DeviceCollectionProducer {
    
    @Autowired private PaoDao paoDao;
    
    private static final String descriptionKey = "yukon.common.device.bulk.bulkAction.collection.addressRange";
    private static final String startParamName = "start";
    private static final String endParamName = "end";
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.addressRange;
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {
        
        DeviceCollectionType type = getSupportedType();
        int startAddress = ServletRequestUtils.getIntParameter(request, type.getParameterName(startParamName), -1);
        int endAddress = ServletRequestUtils.getIntParameter(request, type.getParameterName(endParamName), -1);
        
        Validate.isTrue(startAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(endAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(startAddress <= endAddress, "end address must be greater than start address");
        
        return createDeviceCollection(startAddress, endAddress);
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase base) {
        
        DeviceCollectionType type = base.getCollectionType();
        DeviceCollectionDbType dbType = base.getCollectionDbType();
        
        if (type != DeviceCollectionType.addressRange || dbType != DeviceCollectionDbType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                    + type + ", Persistence type: " + dbType);
        }
        
        Set<DeviceCollectionField> fields = ((DeviceCollectionByField) base).getFields();
        String start = null;
        String end = null;
        for (DeviceCollectionField field : fields) {
            if (field.getName().equalsIgnoreCase(startParamName)) {
                start = field.getValue();
            } else if (field.getName().equalsIgnoreCase(endParamName)) {
                end = field.getValue();
            }
        }
        
        return createDeviceCollection(Integer.parseInt(start), Integer.parseInt(end));
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection collection) {
        
        DeviceCollectionType type = collection.getCollectionType();
        if (type != DeviceCollectionType.addressRange) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        
        String startAddressParameterName = getSupportedType().getParameterName(startParamName);
        String startAddress = collection.getCollectionParameters().get(startAddressParameterName);
        String endAddressParameterName = getSupportedType().getParameterName(endParamName);
        String endAddress = collection.getCollectionParameters().get(endAddressParameterName);
        
        Set<DeviceCollectionField> fields = new HashSet<>();
        fields.add(DeviceCollectionField.of(startParamName, startAddress));
        fields.add(DeviceCollectionField.of(endParamName, endAddress));
        
        return new DeviceCollectionByField(DeviceCollectionType.addressRange, fields);
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
                
                List<PaoIdentifier> paoIdentifiers = paoDao.getPaosByAddressRange(startAddress, endAddress);
                List<SimpleDevice> deviceList = PaoUtils.asSimpleDeviceList(paoIdentifiers);
                
                return deviceList;
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable(descriptionKey, startAddress, endAddress);
            }

            @Override
            public Set<String> getErrorDevices() {
                return null;
            }

            @Override
            public int getDeviceErrorCount() {
                return 0;
            }

            @Override
            public String getUploadFileName() {
                return null;
            }

            @Override
            public String getHeader() {
                return null;
            }
            
        };
    }
    
}
