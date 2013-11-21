package com.cannontech.web.updater.archiveDataAnalysis;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class ArchiveDataAnalysisBackingService implements UpdateBackingService {
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        // split identifier
        String[] idParts = StringUtils.split(identifier, "/");
        int analysisId = Integer.parseInt(idParts[0]);
        String resultTypeStr = idParts[1];
        
        AdaUpdateType type = AdaUpdateType.valueOf(resultTypeStr);
        String value = null;
        switch(type) {
        case DEVICES:
            Integer numberOfDevices = archiveDataAnalysisDao.getNumberOfDevicesInAnalysis(analysisId);
            value =  numberOfDevices.toString();
            break;
        }
        
        return value;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate,
                                               YukonUserContext userContext) {
        return true;
    }
    
    
    private static enum AdaUpdateType {
        DEVICES,
        ;
    }
}
