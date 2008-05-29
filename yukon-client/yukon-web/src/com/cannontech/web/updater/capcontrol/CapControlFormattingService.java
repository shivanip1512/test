package com.cannontech.web.updater.capcontrol;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface CapControlFormattingService<E> {
    
    public enum Format {
        NAME,
        STATE,
        SETUP,
        KVARS_AVAILABLE,
        KVARS_UNAVAILABLE,
        KVARS_CLOSED,
        KVARS_TRIPPED,
        PFACTOR,
        CB_NAME,
        CB_STATUS,
        CB_STATUS_COLOR,
        CB_STATUS_MESSAGE,
        CB_SIZE,
        CB_PARENT,
        DATE_TIME,
        WARNING_FLAG,
        WARNING_FLAG_MESSAGE,
        TARGET,
        TARGET_MESSAGE,
        KVAR_LOAD,
        KVAR_LOAD_MESSAGE,
        KW_VOLTS,
        DAILY_MAX_OPS,
        VERIFICATION_FLAG,
        SA_ENABLED
    }
    
    public String getValueString(E latestValue, Format format, LiteYukonUser user);

}
