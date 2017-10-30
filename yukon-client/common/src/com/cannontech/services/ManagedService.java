package com.cannontech.services;

/**
 * A directory of services that run within Yukon Service Manager. Services should be put in this list if they are
 * critical for normal Yukon functionality. Optional services should instead be described in the YukonServices database 
 * table, where they can be enabled and disabled.
 */
//TODO: (YUK-17006) move most of the services out of the database and into here.
public enum ManagedService {
    SMART_NOTIFICATION("Smart Notification", ServiceType.CONTEXT_FILE_TYPE, "classpath:com/cannontech/services/smartNotification/smartNotificationContext.xml"),
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
