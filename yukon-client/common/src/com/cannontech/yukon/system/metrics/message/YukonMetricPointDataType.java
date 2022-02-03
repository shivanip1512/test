package com.cannontech.yukon.system.metrics.message;

import static com.google.common.base.Preconditions.checkArgument;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum YukonMetricPointDataType {
    RFN_METER_READING_ARCHIVE_REQUESTS_RECEIVED(YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_RECEIVED, 1032,
            PointType.Analog, "RFN Meter Reading Archive Requests Received", ChartInterval.HOUR, ConverterType.DELTA_WATER),
    RFN_METER_READING_ARCHIVE_REQUESTS_POINT_DATA_GENERATED_COUNT(
            YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_POINT_DATA_GENERATED_COUNT, 1034, PointType.Analog,
            "RFN Meter Reading Archive Point Data Generated Count", ChartInterval.HOUR, ConverterType.DELTA_WATER),
    RFN_LCR_READING_ARCHIVE_REQUESTS_RECEIVED(YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_RECEIVED, 1035, PointType.Analog,
            "RFN LCR Reading Archive Requests Received", ChartInterval.HOUR, ConverterType.DELTA_WATER),
    RFN_LCR_READING_ARCHIVE_REQUESTS_QUEUE_SIZE(YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_QUEUE_SIZE, 1036,
            PointType.Analog, "RFN LCR Reading Archive Requests Queue Size", ChartInterval.DAY, ConverterType.RAW),
    RFN_METER_READING_ARCHIVE_REQUESTS_QUEUE_SIZE(YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_QUEUE_SIZE, 1037,
            PointType.Analog, "RFN Meter Reading Archive Requests Queue Size", ChartInterval.DAY, ConverterType.RAW),
    RFN_LCR_READING_POINT_DATA_GENERATED_COUNT(YukonMetricPointInfo.RFN_LCR_READING_POINT_DATA_GENERATED_COUNT, 1038,
            PointType.Analog, "RFN LCR Reading Archive Point Data Generated Count", ChartInterval.HOUR, ConverterType.DELTA_WATER),
    NM_CPU_USAGE(YukonMetricPointInfo.NM_CPU_USAGE, 1039, 
            PointType.Analog, "Network Manager CPU Usage", ChartInterval.HOUR, ConverterType.RAW), 
    NM_MEMORY_USAGE(YukonMetricPointInfo.NM_MEMORY_USAGE, 1040, 
            PointType.Analog, "Network Manager Memory Usage", ChartInterval.HOUR, ConverterType.RAW),
    BILLING_DATA_COUNT(YukonMetricPointInfo.BILLING_DATA_COUNT, 1041, 
            PointType.Analog, "Network Manager Billing reads Count", ChartInterval.HOUR, ConverterType.RAW), 
    INTERVAL_DATA_COUNT(YukonMetricPointInfo.INTERVAL_DATA_COUNT, 1042, 
            PointType.Analog, "Network Manager Interval Data Count", ChartInterval.HOUR, ConverterType.RAW),
    PROFILE_DATA_COUNT(YukonMetricPointInfo.PROFILE_DATA_COUNT, 1043, 
            PointType.Analog, "Network Manager Profiling Data Count", ChartInterval.HOUR, ConverterType.RAW),
    GENERIC_DATA_POINT_COUNT(YukonMetricPointInfo.GENERIC_DATA_POINT_COUNT, 1044, 
            PointType.Analog, "Network Manager LCR Data Count", ChartInterval.HOUR, ConverterType.RAW),
    RPH_INSERTS(YukonMetricPointInfo.RPH_INSERTS, 1045,
            PointType.Analog, "RPH Inserts Generated Count", ChartInterval.FIVEMINUTE, ConverterType.RAW),
    RPH_QUEUE_SIZE(YukonMetricPointInfo.RPH_QUEUE_SIZE, 1046,
            PointType.Analog, "RPH Queue Size", ChartInterval.FIVEMINUTE, ConverterType.RAW);

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
    private ChartInterval chartInterval;
    private ConverterType converterType;



    private YukonMetricPointDataType(YukonMetricPointInfo pointInfo, Integer offset, PointType type, String name,
            ChartInterval chartInterval, ConverterType converterType) {
        this.pointInfo = pointInfo;
        this.offset = offset;
        this.type = type;
        this.name = name;
        this.chartInterval = chartInterval;
        this.converterType = converterType;
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

    public ChartInterval getChartInterval() {
        return chartInterval;
    }

    public ConverterType getConverterType() {
        return converterType;
    }

    public static boolean isPointData(YukonMetricPointInfo pointInfo) {
        checkArgument(pointInfo != null);
        return lookupByYukonMetricPointInfo.contains(pointInfo);
    }

    public static YukonMetricPointDataType getForPointInfo(YukonMetricPointInfo pointInfo) {
        checkArgument(pointInfo != null);
        return lookupByPointInfo.get(pointInfo);
    }

    public static boolean isYukonMetricType(Integer offset, PointType pointType) {
        checkArgument(offset != null);
        checkArgument(pointType != null);
        for (YukonMetricPointDataType metricPointDataType : lookupByPointInfo.values()) {
            if (metricPointDataType.getOffset().equals(offset) && metricPointDataType.getType() == pointType) {
                return true;
            }
        }
        return false;
    }

    public static ChartInterval getChartInterval(Integer offset, PointType pointType) {
        checkArgument(offset != null);
        checkArgument(pointType != null);
        for (YukonMetricPointDataType metricPointDataType : lookupByPointInfo.values()) {
            if (metricPointDataType.getOffset().equals(offset) && metricPointDataType.getType() == pointType) {
                return metricPointDataType.getChartInterval();
            }
        }
        throw new IllegalArgumentException("Invalid Offset and Point type");
    }

    public static ConverterType getConverterType(Integer offset, PointType pointType) {
        checkArgument(offset != null);
        checkArgument(pointType != null);
        for (YukonMetricPointDataType metricPointDataType : lookupByPointInfo.values()) {
            if (metricPointDataType.getOffset().equals(offset) && metricPointDataType.getType() == pointType) {
                return metricPointDataType.getConverterType();
            }
        }
        throw new IllegalArgumentException("Invalid Offset and Point type");
    }
}