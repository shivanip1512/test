package com.cannontech.core.users.model;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceGraphTimeDurationOption implements DisplayableEnum {
    DAY_1(ChartPeriod.DAY),
    WEEK_1(ChartPeriod.WEEK),
    MONTH_1(ChartPeriod.MONTH),
    MONTH_3(ChartPeriod.THREEMONTH),
    YEAR_1(ChartPeriod.YEAR);

    final private ChartPeriod chartPeriod;

    private PreferenceGraphTimeDurationOption(ChartPeriod period) {
        this.chartPeriod = period;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences."+ getParentName() +"."+ name();
    }


    public static String getParentName() {
        return "GRAPH_DISPLAY_TIME_DURATION";
    }

    public static PreferenceGraphTimeDurationOption getDefault() {
        return MONTH_3;
    }

    public ChartPeriod getChartPeriod() {
        return chartPeriod;
    }

    /**
     * Returns null if period is null or NOPERIOD.
     */
    public static PreferenceGraphTimeDurationOption fromChartPeriod(ChartPeriod period) {
        switch (period) {
            case YEAR:
                return YEAR_1;
            case THREEMONTH:
                return MONTH_3;
            case MONTH:
                return MONTH_1;
            case WEEK:
                return WEEK_1;
            case DAY:
                return DAY_1;
            case NOPERIOD:
            default:
                return null;
        }
    }
}