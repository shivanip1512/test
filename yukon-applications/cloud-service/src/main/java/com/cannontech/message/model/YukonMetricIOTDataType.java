package com.cannontech.message.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum YukonMetricIOTDataType {
    GAS_METER_COUNT(YukonMetricPointInfo.GAS_METER_COUNT, "gmcount", "Contains the count of gas meter"),
    DATA_COMPLETENESS_ELECTRIC(YukonMetricPointInfo.DATA_COMPLETENESS_ELECTRIC, "dcelectric",
            "Find the data completeness of RFN electric meters for point type analog for the given time interval of 7 days which is 3 days previous from current time."),
    DATA_COMPLETENESS_WATER(YukonMetricPointInfo.DATA_COMPLETENESS_WATER, "dcwater",
            "Find the data completeness of RFN gas meters for point type analog for the given time interval of 7 days which is 3 days previous from current time."),
    ELECTRIC_METER_COUNT(YukonMetricPointInfo.ELECTRIC_METER_COUNT, "emcount", "Contains the number of enabled electric meter."),
    ELECTRIC_READ_RATE(YukonMetricPointInfo.ELECTRIC_READ_RATE, "electricreadrate",
            "Contains the rate of reading electric meters for last 3 days"),
    WATER_METER_COUNT(YukonMetricPointInfo.WATER_METER_COUNT, "wmcount", "Contains the number of enabled Water meter"),
    WATER_READ_RATE(YukonMetricPointInfo.WATER_READ_RATE, "waterreadrate",
            "Contains the rate of reading water meters for last 3 days"),
    YUKON_VERSION(YukonMetricPointInfo.YUKON_VERSION, "yukversion", "Contains the latest version of Yukon"),
    RELAYS(YukonMetricPointInfo.RELAYS, "rfrelays", "Contains the number of RF relays"),
    RFN_LCR(YukonMetricPointInfo.RFN_LCR, "drcount", "Contains the number of RFN LCRs enabled"),
    RF_METER_DESCENDANT_COUNT(YukonMetricPointInfo.RF_METER_DESCENDANT_COUNT, "meterdescendantcount",
            "Detail of rf meter having highest descendant count"),
    RF_RELAY_DESCENDANT_COUNT(YukonMetricPointInfo.RF_RELAY_DESCENDANT_COUNT, "relaydescendantcount",
            "Detail of relay having highest descendant count"),
    LCR_DESCENDANT_COUNT(YukonMetricPointInfo.LCR_DESCENDANT_COUNT, "lcrdescendantcount",
            "Detail of rf lcr having highest descendant count"),
    BATTERY_NODES(YukonMetricPointInfo.BATTERY_NODES, "batterynodecount",
            "Contains the count of nodes which are battery enabled."),
    CONNECTED_GATEWAYS(YukonMetricPointInfo.CONNECTED_GATEWAYS, "connectedgatewaycount",
            "Count the Gateway whose status id is connected."),
    DUPLICATE_ROUTE_COLORS(YukonMetricPointInfo.DUPLICATE_ROUTE_COLORS, "duprtecolor",
            "Contains the count of gateway which has duplicate route colors."),
    GAP_FILL_REQUESTS_24_HOURS(YukonMetricPointInfo.GAP_FILL_REQUESTS_24_HOURS, "gapfillrequests",
            "Number of requests within 24 hours for Gap fill."),
    GATEWAY_COUNT(YukonMetricPointInfo.GATEWAY_COUNT, "gwcount", "Contains count of gateway"),
    GATEWAY_LOADING_MAX(YukonMetricPointInfo.GATEWAY_LOADING_MAX, "maxnodespergateway",
            "Contains the Gateway with highest node count"),
    GATEWAY_RESET_COUNT_7_DAY(YukonMetricPointInfo.GATEWAY_RESET_COUNT_7_DAY, "gatewayresetcount",
            "Contains the frequency count of gateway reset for last 7 days."),
    GATEWAY_SOCKET_ERROR_COUNT(YukonMetricPointInfo.GATEWAY_SOCKET_ERROR_COUNT, "gwsocketcount",
            "Contains the count of Socket error arises in gateway for last 7 days."),
    GATEWAYS_MISSING_TSYNCS_24_HOUR(YukonMetricPointInfo.GATEWAYS_MISSING_TSYNCS_24_HOUR, "gwsmissingtsync",
            "Gateway missing synchronous time for last 24 hours."),
    NODES_MISSING_ROUTE_TABLE(YukonMetricPointInfo.NODES_MISSING_ROUTE_TABLE, "nodesmissingroutetables",
            "Contains the count of missing node in the route table for last 24 hours."),
    POWERED_NODE_COUNT(YukonMetricPointInfo.POWERED_NODE_COUNT, "powerednodecount",
            "Contains the count of nodes which are power enabled"),
    ROUTE_TABLE_REQUESTS(YukonMetricPointInfo.ROUTE_TABLE_REQUESTS, "routetablerequests",
            "Contains the route table requests for last 24 hours."),
    ROUTE_TABLE_RESPONSES(YukonMetricPointInfo.ROUTE_TABLE_RESPONSES, "routetableresponse",
            "Contains the route table responses for last 24 hours."),
    RF_NETWORK_VERSION(YukonMetricPointInfo.RF_NETWORK_VERSION, "rfversion", "Contains the latest RF network version");

    private final static Set<YukonMetricPointInfo> lookupByYukonMetricPointInfo;
    private final static Map<YukonMetricPointInfo, YukonMetricIOTDataType> lookupByPointInfo;

    static {
        lookupByYukonMetricPointInfo = new HashSet<YukonMetricPointInfo>();
        lookupByPointInfo = new HashMap<YukonMetricPointInfo, YukonMetricIOTDataType>();
        for (YukonMetricIOTDataType type : values()) {
            lookupByYukonMetricPointInfo.add(type.getPointInfo());
            lookupByPointInfo.put(type.getPointInfo(), type);
        }
    }

    private YukonMetricPointInfo pointInfo;
    private String fieldName;
    private String details;

    YukonMetricIOTDataType(YukonMetricPointInfo pointInfo, String fieldName, String details) {
        this.pointInfo = pointInfo;
        this.fieldName = fieldName;
        this.details = details;
    }

    public YukonMetricPointInfo getPointInfo() {
        return pointInfo;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDetails() {
        return details;
    }

    public static boolean isIOTData(YukonMetricPointInfo pointInfo) {
        if (pointInfo == null) {
            throw new IllegalArgumentException();
        }
        return lookupByYukonMetricPointInfo.contains(pointInfo);
    }

    public static YukonMetricIOTDataType getForPointInfo(YukonMetricPointInfo pointInfo) {
        if (pointInfo == null) {
            throw new IllegalArgumentException();
        }
        return lookupByPointInfo.get(pointInfo);
    }
}
