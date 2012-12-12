package com.cannontech.amr.device.search.model;

import com.cannontech.common.i18n.DisplayableEnum;


public enum DeviceSearchField implements DisplayableEnum {
    NAME,
    TYPE,
    ADDRESS,
    METERNUMBER,
    ROUTE,
    COMM_CHANNEL,
    LOAD_GROUP,
    LMGROUP_ROUTE,
    LMGROUP_SERIAL,
    LMGROUP_CAPACITY,
    LMGROUP_TYPE,
    CBC_SERIAL;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.commanderSelect.deviceSearchField." + name();
    }
}
