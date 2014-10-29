package com.cannontech.web.updater.capcontrol;

import com.cannontech.user.YukonUserContext;

public interface CapControlFormattingService<E> {
    
    public enum Format {
        NAME,
        STATE,
        STATE_FLAGS,
        CHILD_COUNT,
        KVARS_AVAILABLE,
        KVARS_UNAVAILABLE,
        KVARS_CLOSED,
        KVARS_TRIPPED,
        PFACTOR,
        CB_NAME,
        CB_STATUS,
        CB_STATUS_COLOR,
        CB_SIZE,
        CB_PARENT,
        CB_PHASEA_BEFORE,
        CB_PHASEB_BEFORE,
        CB_PHASEC_BEFORE,
        CB_BEFORE_TOTAL,
        CB_PHASEA_AFTER,
        CB_PHASEB_AFTER,
        CB_PHASEC_AFTER,
        CB_AFTER_TOTAL,
        CB_PHASEA_PERCENTCHANGE,
        CB_PHASEB_PERCENTCHANGE,
        CB_PHASEC_PERCENTCHANGE,
        CB_PERCENTCHANGE_TOTAL,
        DATE_TIME,
        WARNING_FLAG,
        LOCAL_FLAG,
        WARNING_FLAG_MESSAGE,
        TARGET,
        TARGET_PEAKLAG,
        TARGET_PEAKLEAD,
        TARGET_CLOSEOPENPERCENT,
        TARGET_MESSAGE,
        KVAR_LOAD,
        KVAR_LOAD_EST,
        KVAR_LOAD_QUALITY,
        KVAR_LOAD_MESSAGE,
        WATT_QUALITY,
        VOLT_QUALITY,
        KW,
        VOLTS,
        DAILY_MAX_OPS,
        VERIFICATION_FLAG,
        SA_ENABLED,
        SA_ENABLED_MSG,
        DUALBUS_MESSAGE,
        DUALBUS
    }
    
    public String getValueString(E latestValue, Format format, YukonUserContext userContext);

}