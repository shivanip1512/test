package com.cannontech.common.rfn.message.metadata;

import java.io.Serializable;
import com.cannontech.common.rfn.message.node.WifiSuperMeterData;

public enum RfnMetadata implements Serializable {
    /** Network Manager name in comments */
    COMM_STATUS(CommStatusType.class), // Communication Status
    COMM_STATUS_TIMESTAMP(Long.class), // Communication Status obtained at
    GROUPS(String.class), // Groups
    HARDWARE_VERSION(String.class), // Hardware Version
    IN_NETWORK_TIMESTAMP(Long.class), // In Network Time
    NEIGHBOR_COUNT(Integer.class), // Current Number of Neighbors
    NODE_ADDRESS(String.class), // Node Address
    NODE_FIRMWARE_VERSION(String.class), // Software Version
    NODE_NAMES(String.class), // Node Names
    NODE_SERIAL_NUMBER(String.class), // Node Serial Number
    NODE_TYPE(String.class), // Node Type
    PRIMARY_GATEWAY(String.class), // Primary Gateway
    PRIMARY_GATEWAY_HOP_COUNT(Integer.class), // Number of Hops to Gateway
    PRIMARY_NEIGHBOR(String.class), // Current Primary Neighbor
    PRIMARY_NEIGHBOR_DATA_TIMESTAMP(Long.class), // Current Neighbor Data Timestamp
    PRIMARY_NEIGHBOR_LINK_COST(String.class), // Link Cost to Primary Neighbor
    PRODUCT_NUMBER(String.class), // Product Number
    SUB_MODULE_FIRMWARE_VERSION(String.class), // Zigbee Firmware Version
    NUM_ASSOCIATIONS(Integer.class), // Number of associations
    IPV6_ADDRESS(String.class), // IPv6 address
    HOSTNAME(String.class), // Hostname
    WIFI_SUPER_METER_DATA(WifiSuperMeterData.class); // Wifi super meter related data
    
    private final Class<?> parseType;
    
    private RfnMetadata(Class<?> parseType) {
        this.parseType = parseType;
    }
    
    public Class<?> getParseType() {
        return parseType;
    }
    
    public String getFormatKey() {
        return "yukon.web.widgets.RfnDeviceMetadataWidget." + name();
    }
    
    public boolean isTime() {
        return this ==  IN_NETWORK_TIMESTAMP
                || this == COMM_STATUS_TIMESTAMP
                || this == PRIMARY_NEIGHBOR_DATA_TIMESTAMP;
    }
    
}