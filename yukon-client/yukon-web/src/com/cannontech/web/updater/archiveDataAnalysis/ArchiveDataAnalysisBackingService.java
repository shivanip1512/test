package com.cannontech.web.updater.archiveDataAnalysis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ArchiveDataAnalysisBackingService implements UpdateBackingService {
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        // split identifier
        String[] idParts = StringUtils.split(identifier, "/");
        int analysisId = Integer.parseInt(idParts[0]);
        String resultTypeStr = idParts[1];
        
        AdaUpdateType type = AdaUpdateType.valueOf(resultTypeStr);
        String value = null;
        Integer numberOfDevices = archiveDataAnalysisDao.getNumberOfDevicesInAnalysis(analysisId);
        switch(type) {
            case DEVICES:
                value =  numberOfDevices.toString();
                break;
            case STATUS:
                ObjectNode node = new ObjectMapper().createObjectNode();
                node.put("analysisId", analysisId);
                
                //get status
                Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
                String status = analysis.getStatus().name();
                node.put("status", status);
                
                //get i18ned status string
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String formattedStatus = accessor.getMessage(analysis.getStatus());
                node.put("formattedStatus", formattedStatus);
                
                //determine if there are devices in the analysis and get appropriate tooltip
                String buttonTooltip;
                boolean hasDevices = false;
                if(numberOfDevices > 0) { 
                    hasDevices = true;
                    buttonTooltip = accessor.getMessage("yukon.web.modules.tools.bulk.analysis.list.viewButton.hoverText");
                } else {
                    buttonTooltip = accessor.getMessage("yukon.web.modules.tools.bulk.analysis.list.viewButtonNoDevices.hoverText");
                }
                node.put("hasDevices", hasDevices);
                node.put("buttonTooltip", buttonTooltip);
                
                //get read/analysis resultId
                String statusId = analysis.getStatusId();
                node.put("statusId", statusId);
                
                //return json
                value = node.toString();
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
        STATUS,
        ;
    }
}
