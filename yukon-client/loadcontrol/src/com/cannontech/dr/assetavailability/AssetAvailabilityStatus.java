package com.cannontech.dr.assetavailability;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AssetAvailabilityStatus implements DisplayableEnum {
    
    ACTIVE,
    INACTIVE,
    UNAVAILABLE,
    ;
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.assetDetails.status." + name();
    }

}