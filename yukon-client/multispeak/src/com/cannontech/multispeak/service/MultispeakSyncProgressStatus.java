package com.cannontech.multispeak.service;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakSyncProgressStatus implements DisplayableEnum {

    RUNNING,
    FAILED,
    CANCELED,
    FINISHED;

    private String keyPrefix = "yukon.web.modules.adminSetup.multispeakSync.multispeakSyncProgressStatus.";

    @Override
    public String getFormatKey() {
        return keyPrefix + this;
    }
}
