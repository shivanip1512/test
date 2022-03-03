package com.cannontech.services.systemDataPublisher.service.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SystemDataFieldType implements Serializable {

    public SystemDataFieldType() {

    }
    private static final long serialVersionUID = 1L;

    public enum FieldType {
        DASHBOARD_IOT_CV("dashboardiotcv"),
        GAS_METER_COUNT("gmcount"),
        DATA_COMPLETENESS_ELECTRIC("dcelectric"),
        DATA_COMPLETENESS_WATER("dcwater"),
        ELECTRIC_METER_COUNT("emcount"),
        ELECTRIC_READ_RATE("electricreadrate"),
        WATER_METER_COUNT("wmcount"),
        WATER_READ_RATE("waterreadrate"),
        YUKON_VERSION("yukversion"),
        RFN_RELAYS("rfrelays"),
        RFN_LCR_COUNT("drcount"),
        BATTERY_NODE_COUNT("batterynodecount"),
        CONNECTED_GATEWAY_COUNT("connectedgatewaycount"),
        DUPLICATE_ROUTE_COLOR("duprtecolor"),
        GAP_FILL_REQUESTS("gapfillrequests"),
        GATEWAY_COUNT("gwcount"),
        GATEWAY_LOADING("maxnodespergateway"),
        GATEWAY_RESET_COUNT("gatewayresetcount"),
        GATEWAY_SOCKET_COUNT("gwsocketcount"),
        GATEWAY_MISSING_SYNC("gwsmissingtsync"),
        NODES_MISSING_ROUTE_TABLE("nodesmissingroutetables"),
        POWERED_NODE_COUNT("powerednodecount"),
        ROUTE_TABLE_REQUESTS("routetablerequests"),
        ROUTE_TABLE_RESPONSE("routetableresponse"),
        RFN_VERSION("rfversion"),
        HIGHEST_METER_DESCEDANT_COUNT_DATA("meterdescendantcount"),
        HIGHEST_RELAY_DESCEDANT_COUNT_DATA("relaydescendantcount"),
        HIGHEST_LCR_DESCEDANT_COUNT_DATA("lcrdescendantcount"),
        OTHER,
        ;

        private static final Map<String, FieldType> lookup = Arrays.stream(FieldType.values())
                .filter(s -> s.getStringValue() != null)
                .collect(Collectors.toMap(FieldType::getStringValue, Function.identity()));

        private String stringValue;

        private FieldType(String stringValue) {
            this.stringValue = stringValue;
        }

        private FieldType() {
            stringValue = null;
        }

        private String getStringValue() {
            return stringValue;
        }

        public static FieldType getValue(String stringValue) {
            return lookup.getOrDefault(stringValue, OTHER);
        }
    }

    private FieldType fieldType;
    private String stringValue;

    public SystemDataFieldType(String stringValue) {
        this.fieldType = FieldType.getValue(stringValue);
        this.stringValue = stringValue;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
        ;
        result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SystemDataFieldType other = (SystemDataFieldType) obj;
        if (fieldType != other.fieldType)
            return false;
        if (stringValue == null) {
            if (other.stringValue != null)
                return false;
        } else if (!stringValue.equals(other.stringValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SystemDataFieldType [fieldType=" + fieldType + ", stringValue=" + stringValue + "]";
    }
}
