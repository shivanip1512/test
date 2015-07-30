package com.cannontech.common.bulk.collection.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionField;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableSet;

/**
 * Implementation of DeviceCollectionProducer for devices in an Archive Data Analysis
 */
public class ArchiveDataAnalysisCollectionProducer implements DeviceCollectionProducer {
    
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    
    private static final String key = "yukon.common.device.bulk.bulkAction.collection.archiveDataAnalysis";
    private static final String analysisIdParamName = "analysisId";
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.archiveDataAnalysis;
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        final int analysisId = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName(analysisIdParamName));
        
        return buildDeviceCollection(analysisId);
    }
    
    public DeviceCollection buildDeviceCollection(final int analysisId) {
        
        return new ListBasedDeviceCollection() {
            
            @Override
            public DeviceCollectionType getCollectionType() {
                return getSupportedType();
            }
            
            @Override
            public Map<String, String> getCollectionParameters() {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName(analysisIdParamName), Integer.toString(analysisId));
                return paramMap;
            }
            
            @Override
            public List<SimpleDevice> getDeviceList() {
                List<PaoIdentifier> paoIdentifiers = archiveDataAnalysisDao.getRelevantDeviceIds(analysisId); 
                List<SimpleDevice> deviceList = PaoUtils.asSimpleDeviceList(paoIdentifiers);
                
                return deviceList;
            }
            
            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable(key);
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
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase base) {
        
        DeviceCollectionType type = base.getCollectionType();
        DeviceCollectionDbType dbType = base.getCollectionDbType();
        if (type != DeviceCollectionType.archiveDataAnalysis || dbType != DeviceCollectionDbType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection base. Collection type: " 
                + type + ", Persistence type: " + dbType);
        }
        
        Set<DeviceCollectionField> fields = ((DeviceCollectionByField) base).getFields();
        String analysisId = null;
        
        for (DeviceCollectionField field : fields) {
            if (field.getName().equalsIgnoreCase(analysisIdParamName)) {
                analysisId = field.getValue();
            }
        }
        
        return buildDeviceCollection(Integer.parseInt(analysisId));
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if (type != DeviceCollectionType.archiveDataAnalysis) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        String analysisIdParameterName = getSupportedType().getParameterName(analysisIdParamName);
        String analysisId = deviceCollection.getCollectionParameters().get(analysisIdParameterName);
        DeviceCollectionField field = DeviceCollectionField.of(analysisIdParamName, analysisId);
        
        return new DeviceCollectionByField(DeviceCollectionType.archiveDataAnalysis, ImmutableSet.of(field));
    }
    
}