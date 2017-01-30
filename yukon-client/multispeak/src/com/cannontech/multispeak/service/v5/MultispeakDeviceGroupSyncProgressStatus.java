package com.cannontech.multispeak.service.v5;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakDeviceGroupSyncProgressStatus implements DisplayableEnum {

    RUNNING,
    FAILED,
    CANCELED,
    FINISHED;

    private String keyPrefix = "yukon.web.modules.adminSetup.deviceGroupSync.multispeakDeviceGroupSyncProgressStatus.";

    @Override
    public String getFormatKey() {
        return keyPrefix + this;
    }
}
