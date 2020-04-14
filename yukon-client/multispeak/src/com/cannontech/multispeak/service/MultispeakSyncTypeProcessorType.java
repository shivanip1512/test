package com.cannontech.multispeak.service;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakSyncTypeProcessorType implements DisplayableEnum {
    SUBSTATION,
    BILLING_CYCLE,
    ENROLLMENT,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.adminSetup.multispeakSync.multispeakSyncTypeProcessorType." + this;
    }

    public boolean isEnrollmentType() {
        return this == ENROLLMENT;
    }
}
