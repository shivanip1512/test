package com.cannontech.database.data.pao;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ZoneType implements DatabaseRepresentationSource, DisplayableEnum {
    GANG_OPERATED,
    THREE_PHASE,
    SINGLE_PHASE,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.ivvc.zone." + name();
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
}
