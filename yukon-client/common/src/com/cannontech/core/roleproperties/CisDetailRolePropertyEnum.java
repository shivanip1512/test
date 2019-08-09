package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CisDetailRolePropertyEnum implements DisplayableEnum {

    NONE(null), 
    MULTISPEAK("accountInformationWidget"), 
    CAYENTA("cayentaAccountInformationWidget");

    private String widgetName;

    CisDetailRolePropertyEnum(String widgetName) {
        this.widgetName = widgetName;
    }

    public String getWidgetName() {
        return widgetName;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.common.multispeakCisDetail." + name();
    }
}
