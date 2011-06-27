package com.cannontech.web.bulk.model.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.bulk.model.DeviceCollectionCreationException;
import com.cannontech.web.bulk.model.DeviceCollectionProducer;

/**
 * Implementation of DeviceCollectionProducer for devices in an Archive Data Analysis
 */
public class ArchiveDataAnalysisCollectionProducer implements DeviceCollectionProducer {
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.archiveDataAnalysis;
    }
    
    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {

        final int analysisId = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName("analysisId"));
        
        return buildDeviceCollection(analysisId);
    }
    
    public DeviceCollection buildDeviceCollection(final int analysisId) {
        return new ListBasedDeviceCollection() {
            @Override
            public Map<String, String> getCollectionParameters() {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("analysisId"), Integer.toString(analysisId));
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
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.archiveDataAnalysis", analysisId);
            }
        };
    }
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
}
