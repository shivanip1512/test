package com.cannontech.amr.demandreset.service;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum represents Demand Reset collection action "buckets".
 */
public enum DemandResetBucket implements DisplayableEnum {
    CONFIRMED,
    UNCONFIRMED,
    UNSUPPORTED,
    CANCELED,
    FAILED;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.demandReset." + name();
    }
}
