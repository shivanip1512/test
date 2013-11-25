package com.cannontech.common.bulk.collection.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistenceType;
import com.cannontech.common.bulk.collection.device.persistable.FieldBasedCollectionPersistable;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Maps;

/**
 * Implementation of DeviceCollectionProducer for devices in an Archive Data Analysis
 */
public class ArchiveDataAnalysisCollectionProducer implements DeviceCollectionProducer {
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private static final String ANALYSISID = "analysisId";
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.archiveDataAnalysis;
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {

        final int analysisId = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName(ANALYSISID));
        
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
                paramMap.put(getSupportedType().getParameterName(ANALYSISID), Integer.toString(analysisId));
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
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.archiveDataAnalysis");
            }
        };
    }
    
    @Override
    public DeviceCollection getCollectionFromPersistable(DeviceCollectionPersistable persistable) {
        DeviceCollectionType collectionType = persistable.getCollectionType();
        DeviceCollectionPersistenceType persistenceType = persistable.getPersistenceType();
        if(collectionType != DeviceCollectionType.archiveDataAnalysis || persistenceType != DeviceCollectionPersistenceType.FIELD) {
            throw new IllegalArgumentException("Unable to parse device collection persistable. Collection type: " 
                + collectionType + ", Persistence type: " + persistenceType);
        }
        FieldBasedCollectionPersistable fieldPersistable = (FieldBasedCollectionPersistable) persistable;
        int analysisId = Integer.parseInt(fieldPersistable.getValueMap().get(ANALYSISID));
        
        return buildDeviceCollection(analysisId);
    }
    
    @Override
    public DeviceCollectionPersistable getPersistableFromCollection(DeviceCollection deviceCollection) {
        DeviceCollectionType type = deviceCollection.getCollectionType();
        if(type != DeviceCollectionType.archiveDataAnalysis) {
            throw new IllegalArgumentException("Unable to parse device collection of type " + type);
        }
        String analysisIdParameterName = getSupportedType().getParameterName(ANALYSISID);
        String analysisId = deviceCollection.getCollectionParameters().get(analysisIdParameterName);
        Map<String, String> valueMap = Maps.newHashMap();
        valueMap.put(ANALYSISID, analysisId);
        
        return new FieldBasedCollectionPersistable(DeviceCollectionType.archiveDataAnalysis, valueMap);
    }
}
