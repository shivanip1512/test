package com.cannontech.yukon.system.metrics.listener;

import static com.google.common.base.Preconditions.checkArgument;

import com.cannontech.database.data.point.PointType;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum YukonMetricPointDataType {
    RFN_METER_READING_ARCHIVE_REQUEST_RECEIVED(YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUEST_RECEIVED, 1032,
            PointType.Analog, "Rfn Meter Reading Archive Requests Received"),
    RFN_METER_READING_ARCHIVE_REQUEST_PUSHED(YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUEST_PUSHED, 1033,
            PointType.Analog, "Rfn Meter Reading Archive Requests Pushed"),
    RFN_METER_READING_ARCHIVE_REQUESTS_POINT_DATA_GENERATED_COUNT(
            YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_POINT_DATA_GENERATED_COUNT, 1034, PointType.Analog,
            "Rfn Meter Reading Archive Requests Pt Data Generated Count"),
    RFN_LCR_ARCHIVE_REQUESTS_RECEIVED(YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_RECEIVED, 1035, PointType.Analog,
            "RFN LCR Reading Archive Requests Received");

    private final static ImmutableSet<YukonMetricPointInfo> lookupByYukonMetricPointInfo;
    private final static ImmutableMap<YukonMetricPointInfo, YukonMetricPointDataType> lookupByPointInfo;

    static {
        ImmutableSet.Builder<YukonMetricPointInfo> pointInfoBuilder = ImmutableSet.builder();
        ImmutableMap.Builder<YukonMetricPointInfo, YukonMetricPointDataType> lookupByPointInfoBuilder = ImmutableMap.builder();

        for (YukonMetricPointDataType type : values()) {
            pointInfoBuilder.add(type.getPointInfo());
            lookupByPointInfoBuilder.put(type.getPointInfo(), type);
        }
        lookupByYukonMetricPointInfo = pointInfoBuilder.build();
        lookupByPointInfo = lookupByPointInfoBuilder.build();
    }

    private YukonMetricPointInfo pointInfo;
    private Integer offset;
    private PointType type;
    private String name;

    YukonMetricPointDataType(YukonMetricPointInfo pointInfo, Integer offset, PointType type, String name) {
        this.pointInfo = pointInfo;
        this.offset = offset;
        this.type = type;
        this.name = name;
    }

    public YukonMetricPointInfo getPointInfo() {
        return pointInfo;
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

    public static boolean isPointData(YukonMetricPointInfo pointInfo) {
        checkArgument(pointInfo != null);
        return lookupByYukonMetricPointInfo.contains(pointInfo);
    }

    public static YukonMetricPointDataType getForPointInfo(YukonMetricPointInfo pointInfo) {
        checkArgument(pointInfo != null);
        return lookupByPointInfo.get(pointInfo);
    }
}