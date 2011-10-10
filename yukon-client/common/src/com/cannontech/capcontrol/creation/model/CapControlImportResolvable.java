package com.cannontech.capcontrol.creation.model;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class CapControlImportResolvable {

    private YukonMessageSourceResolvable message;
    private boolean success;
    
    public CapControlImportResolvable(YukonMessageSourceResolvable message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    public void setMessage(YukonMessageSourceResolvable message) {
        this.message = message;
    }
    
    public YukonMessageSourceResolvable getMessage() {
        return message;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }
}
