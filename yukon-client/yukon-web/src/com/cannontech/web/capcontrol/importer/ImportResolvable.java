package com.cannontech.web.capcontrol.importer;

import com.cannontech.common.csvImport.ImportResult;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class ImportResolvable implements ImportResult {
    
    private final YukonMessageSourceResolvable message;
    private final boolean success;
    
    public ImportResolvable(YukonMessageSourceResolvable message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    public YukonMessageSourceResolvable getImportResultMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
}