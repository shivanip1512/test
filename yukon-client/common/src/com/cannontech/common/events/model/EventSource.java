package com.cannontech.common.events.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum EventSource implements Displayable {
    
    
    API,                // EIM web-services
    CONSUMER,           // WEB-INF/pages/stars/consumer
    ACCOUNT_IMPORTER,   // account importer
    OPERATOR,           // WEB-INF/adminSetup/userGroupEditor
                        // WEB-INF/pages/stars/operator
    
    
    //SYSTEM,            // VEE stuff
    //UNSPECIFIED,      
    ;

    private static final String formatKeyBase = "yukon.common.eventSource.";
    
    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault(formatKeyBase + name(), name());
    }

}
