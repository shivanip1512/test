package com.cannontech.common.device.commands.impl;

import static com.cannontech.common.device.DeviceRequestType.*;

import java.util.Map;

import com.cannontech.common.device.DeviceRequestType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;


public class CommandRequestExecutionDefaults {
    private static class CommandRequestExecutionInfo {
        public CommandRequestExecutionInfo(boolean defaultNoqueue, int defaultPriority, boolean scheduled) {
            this.defaultNoqueue = defaultNoqueue;
            this.defaultPriority = defaultPriority;
            this.scheduled = scheduled;
        }
        boolean defaultNoqueue;
        int defaultPriority;
        boolean scheduled;
    }
    private static final Map<DeviceRequestType, CommandRequestExecutionInfo> infoLookup;
    static {
        Builder<DeviceRequestType, CommandRequestExecutionInfo> b = ImmutableMap.builder();
        
        // NOTES:
        // Setting defaultNoqueue = true will cause 'noqueue' to be appended to all commands sent using this type.
        // Setting defaultPriority = X will cause all commands to be sent at priority X
        //
        // IN GENERAL, commands sent to a device (i.e. no specific route) should have a defaultNoqueue = true. If you are using the type with the
        // CommandRequestRouteAndDeviceExecutor or the CommandRequestRouteExecutor, then set defaultNoqueue = false.
        //
        // IN GENERAL, when using a type with a service that results in sending commands to several devices, a lower priority should be used (~8). Consider naming the type
        // with a prefix of "GROUP_". In special cases, such as PHASE_DETECT_COMMAND, the priority is set higher even though this type is used to send commands to multiple devices at a time.
        // Commands intended to be sent to a single device at a time may use a higher priority (~14).
        
        b.put(METER_INFORMATION_PING_COMMAND,                     new CommandRequestExecutionInfo(true, 14, false));
        b.put(PING_DEVICE_ON_ROUTE_COMMAND,                       new CommandRequestExecutionInfo(false, 14, false));
        b.put(PEAK_REPORT_COMMAND,                                new CommandRequestExecutionInfo(true, 14, false));
        b.put(ROUTE_DISCOVERY_PUTCONFIG_COMMAND,                  new CommandRequestExecutionInfo(false, 14, false));
        b.put(CONTROL_CONNECT_DISCONNECT_COMAMND,                 new CommandRequestExecutionInfo(true, 14, false));
        b.put(TOU_SCHEDULE_COMMAND,                               new CommandRequestExecutionInfo(true, 14, false));
        b.put(LM_HARDWARE_COMMAND,                                new CommandRequestExecutionInfo(false, 14, false));
        b.put(INVENTORY_RECONFIG,                                 new CommandRequestExecutionInfo(false, 14, false));
        b.put(MOVE_IN_MOVE_OUT_USAGE_READ,                        new CommandRequestExecutionInfo(false, 14, false));
        b.put(METER_OUTAGES_WIDGET_ATTRIBUTE_READ,                new CommandRequestExecutionInfo(false, 14, false));
        b.put(METER_READINGS_WIDGET_ATTRIBUTE_READ,               new CommandRequestExecutionInfo(false, 14, false));
        b.put(SIMPLE_ATTRIBUTES_WIDGET_ATTRIBUTE_READ,            new CommandRequestExecutionInfo(false, 14, false));
        b.put(TOU_WIDGET_ATTRIBUTE_READ,                          new CommandRequestExecutionInfo(false, 14, false));
        b.put(DISCONNECT_STATUS_ATTRIBUTE_READ,                   new CommandRequestExecutionInfo(false, 14, false));
        b.put(GROUP_COMMAND,                                      new CommandRequestExecutionInfo(false, 8, false));
        b.put(GROUP_ATTRIBUTE_READ,                               new CommandRequestExecutionInfo(false, 8, false));
        b.put(SCHEDULED_GROUP_COMMAND,                            new CommandRequestExecutionInfo(false, 8, true));
        b.put(SCHEDULED_GROUP_ATTRIBUTE_READ,                     new CommandRequestExecutionInfo(false, 8, true));
        b.put(GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ,           new CommandRequestExecutionInfo(false, 8, false));
        b.put(GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ,  new CommandRequestExecutionInfo(false, 8, false));
        b.put(GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET, new CommandRequestExecutionInfo(false, 8, false));
        b.put(GROUP_DEVICE_CONFIG_VERIFY,                         new CommandRequestExecutionInfo(false, 8, false));
        b.put(GROUP_DEVICE_CONFIG_SEND,                           new CommandRequestExecutionInfo(true, 8, false));
        b.put(GROUP_DEVICE_CONFIG_READ,                           new CommandRequestExecutionInfo(true, 8, false));
        b.put(PHASE_DETECT_CLEAR,                                 new CommandRequestExecutionInfo(false, 14, false));
        b.put(PHASE_DETECT_COMMAND,                               new CommandRequestExecutionInfo(false, 14, false));
        b.put(PHASE_DETECT_READ,                                  new CommandRequestExecutionInfo(false, 7, false));
        b.put(VEE_RE_READ,                                        new CommandRequestExecutionInfo(false, 7, false));
        infoLookup = b.build();
    }
	
	
	public static boolean isNoqueue(DeviceRequestType type) {
        return infoLookup.get(type).defaultNoqueue;
    }
	public static int getPriority(DeviceRequestType type) {
        return infoLookup.get(type).defaultPriority;
    }
	public static boolean isScheduled(DeviceRequestType type) {
		return infoLookup.get(type).scheduled;
	}
}
