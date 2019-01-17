package com.cannontech.dr.itron.simulator.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum EditHANDeviceError implements DisplayableEnum {
    GENERIC,
    FATAL_ERROR,
    AUTHORIZATION_FAILURE,
    ESIMACID_PATTERN_MESSAGE,
    ESIASSOCIATE_HANDEVICE_NOTFOUND,
    ESIASSOCIATE_HANDEVICE_UNMODIFIABLE_DEVICESTATE,
    ESIASSOCIATE_HANDEVICE_PROVISIONING_INPROGRESS,
    ESIASSOCIATE_HANDEVICE_ESINOTFOUND,
    ESIASSOCIATE_HANDEVICE_ESINOTVALID,
    ESIASSOCIATE_HANDEVICE_NOTESI
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.itron.editHANDeviceError." + name();

    }  
}
