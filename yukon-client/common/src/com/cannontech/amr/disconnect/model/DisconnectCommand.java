package com.cannontech.amr.disconnect.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DisconnectCommand implements DisplayableEnum {
    ARM,
    CONNECT,
    DISCONNECT
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.disconnect.command."+name();
    }

}
