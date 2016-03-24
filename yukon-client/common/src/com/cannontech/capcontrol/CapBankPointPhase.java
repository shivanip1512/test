package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapBankPointPhase implements DisplayableEnum{
    A, 
    B, 
    C;


    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.capbank.pointPhase." + name();
    }

}