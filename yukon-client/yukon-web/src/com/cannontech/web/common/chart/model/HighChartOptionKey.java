package com.cannontech.web.common.chart.model;

public enum HighChartOptionKey {
    
    COLOR("color"),
    DATE_FORMAT_MONTH_DATE("%b %e"),
    DATE_FORMAT_MONTH_YEAR("%b '%y"),
    DATE_TIME_FORMAT_HOUR_MINUTE("%H:%M"),
    DATE_TIME_FORMAT_DATE_MONTH_HOUR_MINUTE("%b %e %H:%M"),
    FILL_OPACITY("fillOpacity"),
    MARKER("marker"),
    MIN("min"),
    MAX("max"),
    POINT_TOOLTIP("tooltip"), // This is our custom option and not a standard highchart option.
    SERIES_DATA("data"),
    SERIES_GRAPH_TYPE("type"),
    SERIES_X_COORDINATE("x"),
    SERIES_Y_COORDINATE("y"),
    SHOW_IN_LEGEND("showInLegend"),
    TEXT("text"),
    TITLE("title"),
    X_AXIS("xaxis"),
    Y_AXIS("yaxis")
    ;
    
    private final String key;

    private HighChartOptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
