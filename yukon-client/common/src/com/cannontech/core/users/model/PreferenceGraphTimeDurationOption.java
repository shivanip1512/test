package com.cannontech.core.users.model;

import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceGraphTimeDurationOption implements DisplayableEnum {
    DAY_1(ChartPeriod.DAY, Duration.standardDays(1)),
    WEEK_1(ChartPeriod.WEEK, Duration.standardDays(7)),
    MONTH_1(ChartPeriod.MONTH, Duration.standardDays(30)),
    MONTH_3(ChartPeriod.THREEMONTH, Duration.standardDays(90)),
    YEAR_1(ChartPeriod.YEAR, Duration.standardDays(365));

    final private ChartPeriod chartPeriod;
    final private Duration duration;

    private PreferenceGraphTimeDurationOption(ChartPeriod period, Duration duration) {
        this.chartPeriod = period;
        this.duration = duration;
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
        return this.chartPeriod;
    }

    public Date backdate(Date date) {
        return new Instant(date).minus(duration).toDate();
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
            default:
                return null;
        }
    }
}