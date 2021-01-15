package com.cannontech.web.common.chart.model;

public enum HighChartOptionKey {
    
    ALIGN("align"),
    ALIGN_TICKS("alignTicks"),
    BORDER_COLOR("borderColor"),
    BORDER_WIDTH("borderWidth"),
    COLOR("color"),
    DATE_FORMAT_MONTH_DATE("%b %e"),
    DATE_FORMAT_MONTH_YEAR("%b '%y"),
    DATE_TIME_FORMAT_HOUR_MINUTE("%H:%M"),
    DATE_TIME_FORMAT_DATE_MONTH_HOUR_MINUTE("%b %e %H:%M"),
    FILL_OPACITY("fillOpacity"),
    GRID_LINE_WIDTH("gridLineWidth"),
    MARKER("marker"),
    MIN("min"),
    MAX("max"),
    OPPOSITE("opposite"),
    POINT_TOOLTIP("tooltip"), // This is our custom option and not a standard highchart option.
    POINT_WIDTH("pointWidth"),
    ROTATION("rotation"),
    SERIES_DATA("data"),
    SERIES_GRAPH_TYPE("type"),
    SERIES_X_COORDINATE("x"),
    SERIES_Y_COORDINATE("y"),
    SERIES_Y_AXIS("yAxis"),
    SHOW_IN_LEGEND("showInLegend"),
    START_ON_TICK("startOnTick"),
    END_ON_TICK("endOnTick"),
    TEXT("text"),
    THRESHOLD("threshold"),
    TICK_AMOUNT("tickAmount"),
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
