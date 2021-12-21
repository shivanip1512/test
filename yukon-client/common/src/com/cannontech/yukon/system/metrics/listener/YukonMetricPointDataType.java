package com.cannontech.yukon.system.metrics.listener;

import com.cannontech.database.data.point.PointType;

public enum YukonMetricPointDataType {
    RFN_METER_READING_ARCHIVERE_REQUEST_RECEIVED(1032, PointType.Analog),
    RFN_METER_READING_ARCHIVERE_REQUEST_PUSHED(1033, PointType.Analog),
    GAS_METER_COUNT(1034, PointType.Analog);

    private Integer offset;
    private PointType type;

    YukonMetricPointDataType(Integer offset, PointType type) {
        this.offset = offset;
        this.type = type;
    }

    public Integer getOffset() {
        return offset;
    }

    public PointType getType() {
        return type;
    }
}