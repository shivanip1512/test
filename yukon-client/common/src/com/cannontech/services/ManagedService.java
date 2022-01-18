package com.cannontech.services;

/**
 * A directory of services that run within Yukon Service Manager. Services should be put in this list if they are
 * critical for normal Yukon functionality. Optional services should instead be described in the YukonServices database 
 * table, where they can be enabled and disabled.
 */
//TODO: (YUK-17006) move most of the services out of the database and into here.
public enum ManagedService {
    SMART_NOTIFICATION("Smart Notification", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/smartNotification/smartNotificationContext.xml"),
    MAINTENANCE("System Maintenance", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/maintenance/service/maintenanceContext.xml"),
    SERVER_DEVICE_CREATION("Server Device Creation", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/serverDeviceCreation/serverDeviceCreationContext.xml"),
    ECOBEE("Ecobee", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/ecobee/ecobeeContext.xml"),
    NEST_MESSAGE_LISTENER("Nest Message Listener", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/nestMessageListener/nestMessageListenerContext.xml"),
    NM_ALARM_MESSAGE_LISTENER("NM Alarm Message Listener", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/rf/alarms/nmAlarmContext.xml"),
    ITRON_SERVICES("Itron Services", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/itronServices/itronServicesContext.xml"),
    DISCONNECT_METER_SERVICES("Disconnect Meter Services", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/disconnectMeter/disconnectMeterServicesContext.xml"),
    // CLOUD_DATA_LISTENER should always be before SYSTEM_DATA_PUBLISHER as listener should be ready before any data is published in SYSTEM_DATA_PUBLISHER.
    EATON_CLOUD_AUTH_TOKEN("Eaton Cloud Auth Token", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/eatonCloud/authToken/eatonCloudAuthTokenContext.xml"),
    EATON_CLOUD("Eaton Cloud Message Listener", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/dr/lmEatonCloudContext.xml"),
    EATON_CLOUD_DEVICE_AUTO_CREATION("Eaton Cloud Device Auto Creation", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/eatonCloud/creation/eatonCloudDeviceCreationContext.xml"),
    ;
    private final String name;
    private final ServiceType type;
    private final String path;
    
    /**
     * Describe a service managed by Yukon Service Manager.
     * @param name The name of the service.
     * @param type Describes how the service is loaded - via a class name or a Spring context file.
     * @param path The path to the resource describing the service (class file or context file).
     */
    private ManagedService(String name, ServiceType type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public ServiceType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
}
