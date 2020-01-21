package com.cannontech.common.device.commands.impl;

import static com.cannontech.common.device.DeviceRequestType.*;

import java.util.Map;

import com.cannontech.common.device.DeviceRequestType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;


public final class CommandRequestExecutionDefaults {
    private static class ExecutionInfo {
        public ExecutionInfo(boolean defaultNoqueue, int defaultPriority, boolean scheduled) {
            this.defaultNoqueue = defaultNoqueue;
            this.defaultPriority = defaultPriority;
            this.scheduled = scheduled;
        }
        boolean defaultNoqueue;
        int defaultPriority;
        boolean scheduled;
    }
    private static final Map<DeviceRequestType, ExecutionInfo> infoLookup;
    static {
        Builder<DeviceRequestType, ExecutionInfo> b = ImmutableMap.builder();

        // NOTES:
        // Setting defaultNoqueue = true will cause 'noqueue' to be appended to all commands sent
        // using this type.
        // Setting defaultPriority = X will cause all commands to be sent at priority X
        //
        // IN GENERAL, commands sent to a device (i.e. no specific route) should have
        // defaultNoqueue = true. If you are using the type with the
        // CommandRequestRouteAndDeviceExecutor or CommandRequestRouteExecutor, then set
        // defaultNoqueue = false.
        //
        // IN GENERAL, when using a type with a service that results in sending commands to several
        // devices, a lower priority should be used (~8). Consider naming the type with a prefix of
        // "GROUP_". In special cases, such as PHASE_DETECT_COMMAND, the priority is set higher even
        // though this type is used to send commands to multiple devices at a time.
        // Commands intended to be sent to a single device at a time may use a higher priority (~14).

        b.put(METER_INFORMATION_PING_COMMAND,                     new ExecutionInfo(true, 14, false));
        b.put(PING_DEVICE_ON_ROUTE_COMMAND,                       new ExecutionInfo(false, 14, false));
        b.put(PEAK_REPORT_COMMAND,                                new ExecutionInfo(true, 14, false));
        b.put(ROUTE_DISCOVERY_PUTCONFIG_COMMAND,                  new ExecutionInfo(false, 14, false));
        b.put(METER_CONNECT_DISCONNECT_WIDGET,                    new ExecutionInfo(true, 14, false));
        b.put(TOU_SCHEDULE_COMMAND,                               new ExecutionInfo(true, 14, false));
        b.put(LM_HARDWARE_COMMAND,                                new ExecutionInfo(false, 14, false));
        b.put(INVENTORY_RECONFIG,                                 new ExecutionInfo(false, 6, false));
        b.put(MOVE_IN_MOVE_OUT_USAGE_READ,                        new ExecutionInfo(false, 14, false));
        b.put(METER_OUTAGES_WIDGET_ATTRIBUTE_READ,                new ExecutionInfo(false, 14, false));
        b.put(METER_READINGS_WIDGET_ATTRIBUTE_READ,               new ExecutionInfo(false, 14, false));
        b.put(SIMPLE_ATTRIBUTES_WIDGET_ATTRIBUTE_READ,            new ExecutionInfo(false, 14, false));
        b.put(TOU_WIDGET_ATTRIBUTE_READ,                          new ExecutionInfo(false, 14, false));
        b.put(DISCONNECT_STATUS_ATTRIBUTE_READ,                   new ExecutionInfo(false, 14, false));
        b.put(DISCONNECT_COLLAR_PUT_CONFIG_COMMAND,               new ExecutionInfo(false, 14, false));
        b.put(GROUP_CONNECT_DISCONNECT,                           new ExecutionInfo(false, 8, false));
        b.put(METER_DR_CONNECT_DISCONNECT_COMMAND,                new ExecutionInfo(true, 14, false));
        // It is important that GROUP_COMMAND_VERIFY have a lower priority than GROUP_COMMAND.
        // This is used to ensure that a verification gets sent out after the request.
        b.put(GROUP_COMMAND,                                      new ExecutionInfo(false, 8, false));
        b.put(GROUP_COMMAND_VERIFY,                               new ExecutionInfo(false, 7, false));
        b.put(DEMAND_RESET_COMMAND,                               new ExecutionInfo(false, 14, false));
        b.put(DEMAND_RESET_COMMAND_VERIFY,                        new ExecutionInfo(false, 13, false));
        b.put(GROUP_ATTRIBUTE_READ,                               new ExecutionInfo(false, 8, false));
        b.put(SCHEDULED_GROUP_COMMAND,                            new ExecutionInfo(false, 8, true));
        b.put(SCHEDULED_GROUP_ATTRIBUTE_READ,                     new ExecutionInfo(false, 8, true));
        b.put(GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ,           new ExecutionInfo(false, 8, false));
        b.put(GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ,  new ExecutionInfo(false, 8, false));
        b.put(GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET, new ExecutionInfo(false, 8, false));
        b.put(GROUP_DEVICE_CONFIG_VERIFY,                         new ExecutionInfo(false, 8, false));
        b.put(GROUP_DEVICE_CONFIG_SEND,                           new ExecutionInfo(true, 7, false));
        b.put(GROUP_DEVICE_CONFIG_READ,                           new ExecutionInfo(true, 7, false));
        b.put(PHASE_DETECT_CLEAR,                                 new ExecutionInfo(false, 14, false));
        b.put(PHASE_DETECT_COMMAND,                               new ExecutionInfo(false, 14, false));
        b.put(PHASE_DETECT_READ,                                  new ExecutionInfo(false, 7, false));
        b.put(VEE_RE_READ,                                        new ExecutionInfo(false, 7, false));
        b.put(ARCHIVE_DATA_ANALYSIS_LP_READ,                      new ExecutionInfo(false, 7, false));
        b.put(MULTISPEAK_METER_READ_EVENT,                        new ExecutionInfo(true, 13, false));
        b.put(MULTISPEAK_FORMATTED_BLOCK_READ_EVENT,              new ExecutionInfo(true, 13, false));
        b.put(MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND,           new ExecutionInfo(true, 13, false));
        b.put(MULTISPEAK_CONNECT_DISCONNECT,                      new ExecutionInfo(true, 13, false));
        b.put(ASSET_AVAILABILITY_READ,                            new ExecutionInfo(false, 8, false));
        b.put(LM_DEVICE_DETAILS_ATTRIBUTE_READ,                   new ExecutionInfo(false, 8, false));
        b.put(DATA_STREAMING_CONFIG,                              new ExecutionInfo(false, 7, false));
        b.put(METER_PROGRAM_UPLOAD_INITIATE,                      new ExecutionInfo(false, 7, false));
        b.put(METER_PROGRAM_STATUS_READ,                          new ExecutionInfo(false, 7, false));
        b.put(METER_PROGRAM_UPLOAD_CANCEL,                        new ExecutionInfo(false, 8, false));
        b.put(WIFI_METER_CONNECTION_STATUS_REFRESH,               new ExecutionInfo(false, 14, false));
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
