package com.cannontech.common.dr.gear.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ControlStartState implements DisplayableEnum {

    Open(0),
    Close(1);

    ControlStartState(Integer value) {
        this.value = value;
    }

    private Integer value;

    private String baseKey = "yukon.web.modules.dr.setup.gear.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
