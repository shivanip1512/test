package com.cannontech.common.device.commands;


public enum CommandRequestExecutionType {
    
    // NOTES:
    // Setting defaultNoqueue = true will cause 'noqueue' to be appended to all commands sent using this type.
    // Setting defaultPriority = X will cause all commands to be sent at priority X
    //
    // IN GENERAL, commands sent to a device (i.e. no specific route) should have a defaultNoqueue = true. If you are using the type with the
    // CommandRequestRouteAndDeviceExecutor or the CommandRequestRouteExecutor, then set defaultNoqueue = false.
    //
    // IN GENERAL, using a type with a service that results in sending commands to several devices a lower priority should be used (~8). Consider naming the type
    // with a prefix of "GROUP_". In special cases, such as PHASE_DETECT_COMMAND, the priority is set higher even though this type is used to send commands to multiple devices at a time.
    // Commands intended to be sent to a single device at a time may use a higher priority (~14).

    
	METER_INFORMATION_PING_COMMAND(true, 14, "Ping Command", "A 'ping' command sent to device.", false),
	PING_DEVICE_ON_ROUTE_COMMAND(false, 14, "Ping Device On Route Command", "A 'ping' command sent to device on a specific route.", false),
	PEAK_REPORT_COMMAND(true, 14, "Peak Report Command", "A 'getvalue lp peak' command sent for the purpose of profiling.", false),
	ROUTE_DISCOVERY_PUTCONFIG_COMMAND(false, 14, "Route Discovery Put Config Command", "A 'putconfig emetcon intervals' command sent to 410s after a route has been set due to running a route discovery action.", false),
	CONTROL_CONNECT_DISCONNECT_COMAMND(true, 14, "Connect/Disconnect Command", "A 'control connect' or 'control disconnect' command sent from the disconnect widget.", false),
	TOU_SCHEDULE_COMMAND(true, 14, "TOU Schedule Command", "A 'putconfig tou ...' command sent from the TOU widget.", false),
	LM_HARDWARE_COMMAND(false, 14, "LM Hardware Command", "A command sent to LM hardware.", false),
	
	DEVICE_ATTRIBUTE_READ(false, 14, "Device Attribute Read", "An attribute read command sent to a device.", false),
	GROUP_COMMAND(false, 8, "Group Command", "A command sent to a group of devices.", false),
	GROUP_ATTRIBUTE_READ(false, 8, "Group Attribute Read", "An attribute read command sent to a group of devices.", false),
	SCHEDULED_GROUP_COMMAND(false, 8, "Scheduled Group Command", "A command sent to a group of devices periodically.", true),
	SCHEDULED_GROUP_ATTRIBUTE_READ(false, 8, "Scheduled Group Attribute Read", "An attribute read command sent to a group of devices periodically.", true),
	
	GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ(false, 8, "Outage Processing Outage Logs Read", "Outage logs read for the purpose of Outage Processing on the Outages Group of an Outage Monitor.", false),
	GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ(false, 8, "Tamper Flag Processing Internal Flags Read", "Internal flags read for the purpose of Tamper Flag Processing on the Tamper Flag Group of a Tamper Flag Monitor.", false),
	GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET(false, 8, "Tamper Flag Processing Internal Flags Reset", "Internal flags reset for the purpose of Tamper Flag Processing on the Tamper Flag Group of a Tamper Flag Monitor.", false),
	
	GROUP_DEVICE_CONFIG_VERIFY(false, 8, "Device Config Verify", "Verify command sent to a collection of devices for the purpose of Device Configuration.", false),
	GROUP_DEVICE_CONFIG_SEND(true, 8, "Device Config Send", "Send config settings to a collection of devices for the purpose of Device Configuration.", false),
	GROUP_DEVICE_CONFIG_READ(true, 8, "Device Config Read", "Send getconfig install command to a collection of devices for the purpose of Device Configuration.", false),
	
	PHASE_DETECT_CLEAR(false, 14, "Phase Detection Clear Command", "Command sent to broadcast MCT to clear phase data from meters.", false),
	PHASE_DETECT_COMMAND(false, 14, "Phase Detection Command", "Command sent to broadcast MCT to detect a phase change.", false),
	PHASE_DETECT_READ(false, 7, "Phase Detection Read", "Command sent to broadcast MCT to read phase data.", false),
	
	;
	
    private boolean defaultNoqueue;
    private int defaultPriority;
	private String shortName;
	private String description;
	private boolean scheduled;
	
	CommandRequestExecutionType(boolean defaultNoqueue, int defaultPriority, String shortName, String description, boolean scheduled) {
		
	    this.defaultNoqueue = defaultNoqueue;
	    this.defaultPriority = defaultPriority;
		this.shortName = shortName;
		this.description = description;
		this.scheduled = scheduled;
	}
	
	public boolean isNoqueue() {
        return defaultNoqueue;
    }
	public int getPriority() {
        return defaultPriority;
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
