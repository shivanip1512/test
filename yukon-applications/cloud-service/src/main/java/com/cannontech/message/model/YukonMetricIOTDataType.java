package com.cannontech.message.model;

public enum YukonMetricIOTDataType {
    GAS_METER_COUNT("gmcount", "Contains the count of gas meter"),
    DATA_COMPLETENESS_ELECTRIC("dcelectric",
            "Find the data completeness of RFN electric meters for point type analog for the given time interval of 7 days which is 3 days previous from current time."),
    DATA_COMPLETENESS_WATER("dcwater",
            "Find the data completeness of RFN gas meters for point type analog for the given time interval of 7 days which is 3 days previous from current time."),
    ELECTRIC_METER_COUNT("emcount", "Contains the number of enabled electric meter."),
    ELECTRIC_READ_RATE("electricreadrate", "Contains the rate of reading electric meters for last 3 days"),
    WATER_METER_COUNT("wmcount", "Contains the number of enabled Water meter"),
    WATER_READ_RATE("waterreadrate", "Contains the rate of reading water meters for last 3 days"),
    YUKON_VERSION("yukversion", "Contains the latest version of Yukon"),
    RELAYS("rfrelays", "Contains the number of RF relays"),
    RFN_LCR("drcount", "Contains the number of RFN LCRs enabled"),
    RF_METER_DESCENDANT_COUNT("meterdescendantcount", "Detail of rf meter having highest descendant count"),
    RF_RELAY_DESCENDANT_COUNT("relaydescendantcount", "Detail of relay having highest descendant count"),
    LCR_DESCENDANT_COUNT("lcrdescendantcount", "Detail of rf lcr having highest descendant count"),
    BATTERY_NODES("batterynodecount", "Contains the count of nodes which are battery enabled."),
    CONNECTED_GATEWAYS("connectedgatewaycount", "Count the Gateway whose status id is connected."),
    DUPLICATE_ROUTE_COLORS("duprtecolor", "Contains the count of gateway which has duplicate route colors."),
    GAP_FILL_REQUESTS_24_HOURS("gapfillrequests", "Number of requests within 24 hours for Gap fill."),
    GATEWAY_COUNT("gwcount", "Contains count of gateway"),
    GATEWAY_LOADING_MAX("maxnodespergateway", "Contains the Gateway with highest node count"),
    GATEWAY_RESET_COUNT_7_DAY("gatewayresetcount", "Contains the frequency count of gateway reset for last 7 days."),
    GATEWAY_SOCKET_ERROR_COUNT("gwsocketcount", "Contains the count of Socket error arises in gateway for last 7 days."),
    GATEWAYS_MISSING_TSYNCS_24_HOUR("gwsmissingtsync", "Gateway missing synchronous time for last 24 hours."),
    NODES_MISSING_ROUTE_TABLE("nodesmissingroutetables",
            "Contains the count of missing node in the route table for last 24 hours."),
    POWERED_NODE_COUNT("powerednodecount", "Contains the count of nodes which are power enabled"),
    ROUTE_TABLE_REQUESTS("routetablerequests", "Contains the route table requests for last 24 hours."),
    ROUTE_TABLE_RESPONSES("routetableresponse", "Contains the route table responses for last 24 hours."),
    RF_NETWORK_VERSION("rfversion", "Contains the latest RF network version");

    private String fieldName;
    private String details;

    YukonMetricIOTDataType(String fieldName, String details) {
        this.fieldName = fieldName;
        this.details = details;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDetails() {
        return details;
    }

}
