package com.cannontech.common.bulk.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AdaStatus implements DisplayableEnum {
    RUNNING,  //Analysis in progress
    COMPLETE, //Analysis complete, no actions being carried out
    READING,  //"LP holes" reads in progress
    DELETED,  //Analysis is being deleted, do not display
    ;
    
    private final String keyPrefix = "yukon.web.modules.tools.bulk.analysis.";
    
    public boolean isRunning() {
        return this == RUNNING;
    }
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

}
