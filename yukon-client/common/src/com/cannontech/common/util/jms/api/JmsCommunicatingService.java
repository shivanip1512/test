package com.cannontech.common.util.jms.api;

/**
 * Describes possible senders and receivers of JMS messages.
 */
public enum JmsCommunicatingService {
    NETWORK_MANAGER("Network Manager"),
    YUKON_EIM("Yukon EIM (SOAP Web Services)"),
    YUKON_FIELD_SIMULATOR("Yukon Field Simulator"),
    YUKON_PORTER("Yukon Porter"),
    YUKON_SERVICE_MANAGER("Yukon Service Manager"),
    YUKON_SIMULATORS("Yukon Simulators"),
    YUKON_WEBSERVER("Yukon Web Server"),
    YUKON_WEBSERVER_DEV_PAGES("Yukon Web Server Dev Pages"),
    YUKON_MESSAGE_BROKER("Yukon Message Broker"),
    YUKON_WATCHDOG("Yukon Watchdog"),
    YUKON_LOAD_MANAGEMENT("Yukon Load Management"),
    YUKON_CLOUD_SERVICE("Yukon Cloud Service")
    ;
    
    private final String niceString;
    
    private JmsCommunicatingService(String niceString) {
        this.niceString = niceString;
    }
    
    @Override
    public String toString() {
        return niceString;
    }
}
