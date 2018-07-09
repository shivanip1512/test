package com.cannontech.common.pao.notes.filter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PaoSelectionMethod implements DisplayableEnum {

    allDevices,
    selectIndividually,
    byDeviceGroups;

    @Override
    public String getFormatKey() {
        return "yukon.web.common.paoNoteSearch." + name();
    }
}
