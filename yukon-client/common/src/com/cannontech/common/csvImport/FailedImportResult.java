package com.cannontech.common.csvImport;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class FailedImportResult implements ImportResult {
    String key;
    Object[] arguments;
    
    public FailedImportResult(String key, String... arguments) {
        this.key = key;
        this.arguments = arguments;
    }
    
    @Override
    public YukonMessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(key, arguments);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

}
