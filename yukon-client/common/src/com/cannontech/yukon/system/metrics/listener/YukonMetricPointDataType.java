package com.cannontech.yukon.system.metrics.listener;

import com.cannontech.database.data.point.PointType;

public enum YukonMetricPointDataType {
    RFN_METER_READING_ARCHIVERE_REQUEST_RECEIVED(1032, PointType.Analog),
    RFN_METER_READING_ARCHIVERE_REQUEST_PUSHED(1033, PointType.Analog),
    GAS_METER_COUNT(1034, PointType.Analog),
    DATA_COMPLETENESS_ELECTRIC(1035, PointType.Analog),
    DATA_COMPLETENESS_WATER(1036, PointType.Analog),
    ELECTRIC_METER_COUNT(1037, PointType.Analog),
    RFN_LCR_COUNT(1038, PointType.Analog),
    RFN_RELAYS(1039, PointType.Analog),
    WATER_METER_COUNT(1040, PointType.Analog),
    ELECTRIC_READ_RATE(1041, PointType.Analog),
    WATER_READ_RATE(1042, PointType.Analog),
    HIGHEST_LCR_DESCEDANT_COUNT_DATA(1043, PointType.Analog),
    HIGHEST_METER_DESCEDANT_COUNT_DATA(1044, PointType.Analog),
    HIGHEST_RELAY_DESCEDANT_COUNT_DATA(1045, PointType.Analog),
    YUKON_VERSION(1046, PointType.Analog);

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