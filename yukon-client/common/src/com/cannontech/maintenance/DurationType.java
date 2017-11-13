package com.cannontech.maintenance;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DurationType implements DisplayableEnum {
    THIRTEEN_MONTHS(13),
    TWO_YEARS(24),
    THREE_YEARS(36),
    FOUR_YEARS(48),
    FIVE_YEARS(60);

    private int duration;

    private DurationType(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.durationType." + name();
    }

}
