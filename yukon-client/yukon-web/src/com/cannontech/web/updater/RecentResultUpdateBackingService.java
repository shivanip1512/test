package com.cannontech.web.updater;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.user.YukonUserContext;

public abstract class RecentResultUpdateBackingService implements UpdateBackingService {

    private ObjectFormattingService objectFormattingService;
    
    public abstract Object getResultValue(String resultId, String resultTypeStr);
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        // split identifier
        String[] idParts = StringUtils.split(identifier, "/");
        String resultId = idParts[0];
        String resultTypeStr = idParts[1];
        
        // get result value
        Object value = getResultValue(resultId, resultTypeStr);
        return objectFormattingService.formatObjectAsString(value, userContext);
        
    }

    @Autowired
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }
}