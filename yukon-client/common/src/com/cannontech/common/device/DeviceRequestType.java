package com.cannontech.common.device;

public enum DeviceRequestType {
    
    METER_INFORMATION_PING_COMMAND("Ping Command", "A 'ping' command sent to device.", false),
    PING_DEVICE_ON_ROUTE_COMMAND("Ping Device On Route Command", "A 'ping' command sent to device on a specific route.", false),
    PEAK_REPORT_COMMAND("Peak Report Command", "A 'getvalue lp peak' command sent for the purpose of profiling.", false),
    ROUTE_DISCOVERY_PUTCONFIG_COMMAND("Route Discovery Put Config Command", "A 'putconfig emetcon intervals' command sent to 410s after a route has been set due to running a route discovery action.", false),
    METER_CONNECT_DISCONNECT_WIDGET("Connect/Disconnect Command", "A 'control connect' or 'control disconnect' command sent from the disconnect widget.", false),
    TOU_SCHEDULE_COMMAND("TOU Schedule Command", "A 'putconfig tou ...' command sent from the TOU widget.", false),
    LM_HARDWARE_COMMAND("LM Hardware Command", "A command sent to LM hardware.", false),
    INVENTORY_RECONFIG("Inventory Reconfiguration", "A command sent to LM hardware as a result of inventory reconfiguration.", false),
    MOVE_IN_MOVE_OUT_USAGE_READ("Move In/Move Out Usage Reading", "Move In/Move Out Usage Reading", false),
    METER_DR_CONNECT_DISCONNECT_COMMAND("Meter DR Connect/Disconnect Command", "A 'control connect' or 'control disconnect' Meter DR command", false),
    
    METER_OUTAGES_WIDGET_ATTRIBUTE_READ("Outage Widget Attribute Read", "Reads all meter attributes for display in the Meter Outages Widget.", false),
    METER_READINGS_WIDGET_ATTRIBUTE_READ("Meter Readings Widget Attribute Read", "Reads all meter attributes that are displayed in the Meter Readings Widget.", false),
    SIMPLE_ATTRIBUTES_WIDGET_ATTRIBUTE_READ("Simple Attributes Widget Attribute Read", "Reads a set of attributes for the Simple Attributes Widget.", false),
    TOU_WIDGET_ATTRIBUTE_READ("TOU Widget Attribute Read", "Reads TOU attributes for the TOU Widget.", false),
    DISCONNECT_STATUS_ATTRIBUTE_READ("Disconnect Status Attribute Read", "Disconnect Status Attribute Read", false), 
    DISCONNECT_COLLAR_PUT_CONFIG_COMMAND("Disconnect Collar Configuration Upload Command", "Sends Disconnect Collar Configuration to the meter", false),
    
    GROUP_COMMAND("Group Command", "A command sent to a group of devices.", false),
    GROUP_COMMAND_VERIFY("Group Command Verify", "A command sent to a group of devices to verify.", false),
    DEMAND_RESET_COMMAND("Demand Reset Command", "Demand reset command sent to a group of devices.", false),
    DEMAND_RESET_COMMAND_VERIFY("Demand Reset Command Verify", "A command sent to a group of devices to verify demand reset.", false),
    GROUP_ATTRIBUTE_READ("Group Attribute Read", "An attribute read command sent to a group of devices.", false),
    SCHEDULED_GROUP_COMMAND("Scheduled Group Command", "A command sent to a group of devices periodically.", true),
    SCHEDULED_GROUP_ATTRIBUTE_READ("Scheduled Group Attribute Read", "An attribute read command sent to a group of devices periodically.", true),
    GROUP_CONNECT_DISCONNECT("Group Connect/Disconnect", "A 'control connect' or 'control disconnect' command sent to a group of devices.", false),
    
    GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ("Outage Processing Outage Logs Read", "Outage logs read for the purpose of Outage Processing on the Outages Group of an Outage Monitor.", false),
    GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ("Tamper Flag Processing Internal Flags Read", "Internal flags read for the purpose of Tamper Flag Processing on the Tamper Flag Group of a Tamper Flag Monitor.", false),
    GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET("Tamper Flag Processing Internal Flags Reset", "Internal flags reset for the purpose of Tamper Flag Processing on the Tamper Flag Group of a Tamper Flag Monitor.", false),
    
    GROUP_DEVICE_CONFIG_VERIFY("Device Config Verify", "Verify command sent to a collection of devices for the purpose of Device Configuration.", false),
    GROUP_DEVICE_CONFIG_SEND("Device Config Send", "Send config settings to a collection of devices for the purpose of Device Configuration.", false),
    GROUP_DEVICE_CONFIG_READ("Device Config Read", "Send getconfig install command to a collection of devices for the purpose of Device Configuration.", false),
    
    PHASE_DETECT_CLEAR("Phase Detection Clear Command", "Command sent to broadcast MCT to clear phase data from meters.", false),
    PHASE_DETECT_COMMAND("Phase Detection Command", "Command sent to broadcast MCT to detect a phase change.", false),
    PHASE_DETECT_READ("Phase Detection Read", "Command sent to MCT's to read phase data.", false),
    VEE_RE_READ("Validation Engine Automatic Read", "Command sent to meter to validate previous readings", false),
    
    ARCHIVE_DATA_ANALYSIS_LP_READ("Archive Data Analysis Load Profile Read", "Command sent to MCT to read load profile data that was previously missed", false),
    MULTISPEAK_METER_READ_EVENT("MultiSpeak Meter Read Event", "Command sent to meters due to a MultiSpeak request.",false),
    MULTISPEAK_FORMATTED_BLOCK_READ_EVENT("MultiSpeak FormattedBlock Read Event", "Command sent to meters due to a MultiSpeak FormattedBlock request.",false),
    MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND("Ping Command", "A 'ping' command sent to a meters due to a MultiSpeak OD request.", false),
    MULTISPEAK_CONNECT_DISCONNECT("MultiSpeak Connect/Disconnect Event", "A 'control connect' or 'control disconnect' command sent to one to many meters.", false),
    ASSET_AVAILABILITY_READ("Asset Availability Read", "Command sent to two-way LCRs to determine their availability.", false),
    LM_DEVICE_DETAILS_ATTRIBUTE_READ("Load Management Details Attribute Read", "Command sent to read two-way LCRs." ,false),
    DATA_STREAMING_CONFIG("Data Streaming Configuration Command", "Command sent to configure RFN data streaming.", false),
    
    METER_PROGRAM_UPLOAD_INITIATE("Meter Programing Upload Initiation Command", "Command sent to initiate program upload to a meter.", false),
    METER_PROGRAM_UPLOAD_CANCEL("Meter Programing Upload Cancel Command", "Command sent to cancel program upload to a meter.", false),
    METER_PROGRAM_STATUS_READ("Meter Programming Status Read Command", "Command sent to retrieve program status for a meter.", false),
    
    WIFI_METER_CONNECTION_STATUS_REFRESH("Wi-Fi Meter Connection Status Refresh Command", "Command sent to Wi-Fi meters to have them update connection status.", false)
    ;
    
    private String shortName;
    private String description;
    private boolean scheduled; // not sure about this guy
 
    DeviceRequestType(String shortName, String description, boolean scheduled) {
        this.shortName = shortName;
        this.description = description;
        this.scheduled = scheduled;
    }
    
    public String getShortName() {
        return shortName;
    }
    public String getDescription() {
        return description;
    }
    public boolean isScheduled() {
        return scheduled;
    }
}
