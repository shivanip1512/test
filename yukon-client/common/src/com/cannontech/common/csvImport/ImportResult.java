package com.cannontech.common.csvImport;

import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Interface for classes that represent the results of an import attempt. This allows various
 * result types to be intermingled and read, even if their internals are different.
 */
public interface ImportResult {
    public YukonMessageSourceResolvable getMessage();
    public boolean isSuccess();
}
