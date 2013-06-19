package com.cannontech.core.users.model;

import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.Instant;

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
        return YEAR_1;
    }

    public ChartPeriod getChartPeriod() {
        return this.chartPeriod;
    }

    public Date backdate(Date date) {
        Instant instant = new Instant(date);
        switch (this) {
            case YEAR_1: 
                instant.minus(Duration.standardDays(365));
                break;
            case MONTH_3:
                instant.minus(Duration.standardDays(90));
                break;
            case MONTH_1:
                instant.minus(Duration.standardDays(30));
                break;
            case WEEK_1:
                instant.minus(Duration.standardDays(7));
                break;
            case DAY_1:
                instant.minus(Duration.standardDays(1));
                break;
        }
        return instant.toDate();
    }

    /**
     * 
     * @param period    ChartPeriod
     * @return null if period is null or NOPERIOD.
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
                return null;
        }
        return null;
    }
}