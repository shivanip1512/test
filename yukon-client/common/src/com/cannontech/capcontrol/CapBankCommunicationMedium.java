package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapBankCommunicationMedium implements DisplayableEnum{
    NONE("None"), 
    PAGING("Paging"), 
    DLC("DLC"), 
    VHF("VHF"),
    ONEXRTT("1XRTT"),
    GPRS("GPRS"),
    SSRADIO("SSRadio");

    private final String displayName;

    private CapBankCommunicationMedium(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.capbank.communicationMedium." + name();
    }

}