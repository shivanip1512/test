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
            YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_POINT_DATA_GENERATED_COUNT, 1034,
            PointType.Analog, "RFN Meter Reading Archive Point Data Generated Count", ChartInterval.HOUR,
            ConverterType.DELTA_WATER),
    RFN_LCR_READING_ARCHIVE_REQUESTS_RECEIVED(YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_RECEIVED, 1035,
            PointType.Analog, "RFN LCR Reading Archive Requests Received", ChartInterval.HOUR, ConverterType.DELTA_WATER),
    RFN_LCR_READING_ARCHIVE_REQUESTS_QUEUE_SIZE(YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_QUEUE_SIZE, 1036,
            PointType.Analog, "RFN LCR Reading Archive Requests Queue Size", ChartInterval.DAY, ConverterType.RAW),
    RFN_METER_READING_ARCHIVE_REQUESTS_QUEUE_SIZE(YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_QUEUE_SIZE, 1037,
            PointType.Analog, "RFN Meter Reading Archive Requests Queue Size", ChartInterval.DAY, ConverterType.RAW),
    RFN_LCR_READING_POINT_DATA_GENERATED_COUNT(YukonMetricPointInfo.RFN_LCR_READING_POINT_DATA_GENERATED_COUNT, 1038,
            PointType.Analog, "RFN LCR Reading Archive Point Data Generated Count", ChartInterval.HOUR,
            ConverterType.DELTA_WATER),
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
            PointType.Analog, "RawPointHistory Rows Inserted", ChartInterval.FIVEMINUTE, ConverterType.RAW),
    RPH_QUEUE_SIZE(YukonMetricPointInfo.RPH_QUEUE_SIZE, 1046,
            PointType.Analog, "RawPointHistory Archiver Queue Size", ChartInterval.FIVEMINUTE, ConverterType.RAW),
    RPH_INSERT_MILLIS_PER_ROW(YukonMetricPointInfo.RPH_INSERT_MILLIS_PER_ROW, 1047,
            PointType.Analog, "RawPointHistory Average Insert Time Per Row", ChartInterval.FIVEMINUTE, ConverterType.RAW),
    ELECTRIC_METER_COUNT(YukonMetricPointInfo.ELECTRIC_METER_COUNT, 1048,
            PointType.Analog, "Electric Meter Count"),
    GAS_METER_COUNT(YukonMetricPointInfo.GAS_METER_COUNT, 1049,
            PointType.Analog, "Gas Meter Count"),
    GATEWAY_COUNT(YukonMetricPointInfo.GATEWAY_COUNT, 1050,
            PointType.Analog, "Gateway Count"),
    WATER_METER_COUNT(YukonMetricPointInfo.WATER_METER_COUNT, 1051,
            PointType.Analog, "Water Meter Count"),
    RELAYS(YukonMetricPointInfo.RELAYS, 1052,
            PointType.Analog, "Relays"),
    RFN_LCR(YukonMetricPointInfo.RFN_LCR, 1053,
            PointType.Analog, "RFN LCR"),
    DATA_COMPLETENESS_ELECTRIC(YukonMetricPointInfo.DATA_COMPLETENESS_ELECTRIC, 1054,
            PointType.Analog, "Data Completeness Electric"),
    DATA_COMPLETENESS_WATER(YukonMetricPointInfo.DATA_COMPLETENESS_WATER, 1055,
            PointType.Analog, "Data Completeness Water"),
    ELECTRIC_READ_RATE(YukonMetricPointInfo.ELECTRIC_READ_RATE, 1056,
            PointType.Analog, "Electric Read Rate"),
    WATER_READ_RATE(YukonMetricPointInfo.WATER_READ_RATE, 1057,
            PointType.Analog, "Water Read Rate"),
    GAP_FILL_REQUESTS_24_HOURS(YukonMetricPointInfo.GAP_FILL_REQUESTS_24_HOURS, 1058,
            PointType.Analog, "Gap Fill Requests (24 Hours)"),
    GATEWAY_RESET_COUNT_7_DAY(YukonMetricPointInfo.GATEWAY_RESET_COUNT_7_DAY, 1059,
            PointType.Analog, "Gateway Reset Count (7 Day)"),
    GATEWAYS_MISSING_TSYNCS_24_HOUR(YukonMetricPointInfo.GATEWAYS_MISSING_TSYNCS_24_HOUR, 1060,
            PointType.Analog, "Gateways Missing TSyncs (24 Hour)"),
    GATEWAY_SOCKET_ERROR_COUNT(YukonMetricPointInfo.GATEWAY_SOCKET_ERROR_COUNT, 1061,
            PointType.Analog, "Gateway Socket Error Count"),
    NODES_MISSING_ROUTE_TABLE(YukonMetricPointInfo.NODES_MISSING_ROUTE_TABLE, 1062,
            PointType.Analog, "Nodes Missing Route Table"),
    ROUTE_TABLE_REQUESTS(YukonMetricPointInfo.ROUTE_TABLE_REQUESTS, 1063,
            PointType.Analog, "Route Table Requests"),
    ROUTE_TABLE_RESPONSES(YukonMetricPointInfo.ROUTE_TABLE_RESPONSES, 1064,
            PointType.Analog, "Route Table Responses"),
    RF_NETWORK_VERSION(YukonMetricPointInfo.RF_NETWORK_VERSION, 1065,
            PointType.Analog, "RF Network Version");
    
    private final static ImmutableSet<YukonMetricPointInfo> lookupByYukonMetricPointInfo;
    private final static ImmutableMap<YukonMetricPointInfo, YukonMetricPointDataType> lookupByPointInfo;
    private final static ImmutableMap<YukonMetricPointInfo, YukonMetricPointDataType> lookupByDispalyablePointInfo;

    static {
        ImmutableSet.Builder<YukonMetricPointInfo> pointInfoBuilder = ImmutableSet.builder();
        ImmutableMap.Builder<YukonMetricPointInfo, YukonMetricPointDataType> lookupByPointInfoBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<YukonMetricPointInfo, YukonMetricPointDataType> lookupByDispalyablePointInfoBuilder = ImmutableMap.builder();

        for (YukonMetricPointDataType type : values()) {
            pointInfoBuilder.add(type.getPointInfo());
            lookupByPointInfoBuilder.put(type.getPointInfo(), type);
            if (type.getChartInterval() != null && type.getConverterType() != null) {
                lookupByDispalyablePointInfoBuilder.put(type.getPointInfo(), type);
            }
        }
        lookupByYukonMetricPointInfo = pointInfoBuilder.build();
        lookupByPointInfo = lookupByPointInfoBuilder.build();
        lookupByDispalyablePointInfo = lookupByDispalyablePointInfoBuilder.build();
    }

    private YukonMetricPointInfo pointInfo;
    private Integer offset;
    private PointType type;
    private String name;
    private ChartInterval chartInterval;
    private ConverterType converterType;


    private YukonMetricPointDataType(YukonMetricPointInfo pointInfo, Integer offset, PointType type, String name) {
        this.pointInfo = pointInfo;
        this.offset = offset;
        this.type = type;
        this.name = name;
    }

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

    public static boolean isYukonMetricTypeDisplayable(Integer offset, PointType pointType) {
        checkArgument(offset != null);
        checkArgument(pointType != null);
        for (YukonMetricPointDataType metricPointDataType : lookupByDispalyablePointInfo.values()) {
            if (metricPointDataType.getOffset().equals(offset) && metricPointDataType.getType() == pointType) {
                return true;
            }
        }
        return false;
    }

    public static ChartInterval getChartInterval(Integer offset, PointType pointType) {
        checkArgument(offset != null);
        checkArgument(pointType != null);
        for (YukonMetricPointDataType metricPointDataType : lookupByDispalyablePointInfo.values()) {
            if (metricPointDataType.getOffset().equals(offset) && metricPointDataType.getType() == pointType) {
                return metricPointDataType.getChartInterval();
            }
        }
        throw new IllegalArgumentException("Invalid Offset and Point type");
    }

    public static ConverterType getConverterType(Integer offset, PointType pointType) {
        checkArgument(offset != null);
        checkArgument(pointType != null);
        for (YukonMetricPointDataType metricPointDataType : lookupByDispalyablePointInfo.values()) {
            if (metricPointDataType.getOffset().equals(offset) && metricPointDataType.getType() == pointType) {
                return metricPointDataType.getConverterType();
            }
        }
        throw new IllegalArgumentException("Invalid Offset and Point type");
    }
}