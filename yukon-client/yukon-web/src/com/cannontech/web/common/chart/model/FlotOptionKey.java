package com.cannontech.web.common.chart.model;

/**
 * See specific details on these FlotChart options (and the rest of the options) here:
 * https://github.com/flot/flot/blob/master/API.md
 * 
 * These options below are just the ones that we are using (in Java).
 * We are using some more options (as defaults) in yukon.flot.js that aren't here
 */
public enum FlotOptionKey {
    SERIES("series"),
    SERIES_BARS("bars"),
    SERIES_BARS_BARWIDTH("barWidth"),
    XAXIS("xaxis"),
    XAXIS_POSITION("position"),
    XAXIS_AXISLABEL("axisLabel"),
    XAXIS_MODE("mode"),
    XAXIS_MIN("min"),
    XAXIS_MAX("max"),
    XAXIS_AUTOSCALEMARGIN("autoscaleMargin"),
    YAXIS("yaxis"),
    YAXES("yaxes"),
    YAXIS_POSITION("position"),
    YAXIS_AXISLABEL("axisLabel"),
    YAXIS_MODE("mode"),
    YAXIS_MIN("min"),
    YAXIS_MAX("max"),
    YAXIS_AUTOSCALEMARGIN("autoscaleMargin"),
    GRID("grid"),
    GRID_MARKINGS("markings"),
    GRID_MARKINGS_COLOR("color"),
    GRID_MARKINGS_YAXIS("yaxis"),
    GRID_MARKINGS_YAXIS_FROM("from"),
    GRID_MARKINGS_YAXIS_TO("to"),
    ;

    private final String key;

    private FlotOptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
