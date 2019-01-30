package com.cannontech.common.stars.scheduledDataImport;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AssetImportResultType implements DisplayableEnum {
    
    ALL_IMPORTS,
    IMPORTS_WITH_ERRORS;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.operator.assetImportResultType." + this.name();
    }

}
