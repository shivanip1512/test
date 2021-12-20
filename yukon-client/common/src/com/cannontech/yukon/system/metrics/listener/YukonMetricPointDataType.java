package com.cannontech.yukon.system.metrics.listener;

import com.cannontech.database.data.point.PointType;

public enum YukonMetricPointDataType {
    RFN_METER_READING_ARCHIVE_REQUEST_RECEIVED(1032, PointType.Analog, "Rfn Meter Reading Archive Requests Received"),
    RFN_METER_READING_ARCHIVE_REQUEST_PUSHED(1033, PointType.Analog, "Rfn Meter Reading Archive Requests Pushed");

    private Integer offset;
    private PointType type;
    private String name;

    YukonMetricPointDataType(Integer offset, PointType type, String name) {
        this.offset = offset;
        this.type = type;
        this.name = name;
    }

    public Integer getOffset() {
        return offset;
    }

    public PointType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}