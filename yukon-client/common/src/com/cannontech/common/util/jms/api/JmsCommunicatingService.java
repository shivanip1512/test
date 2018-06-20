package com.cannontech.common.util.jms.api;

/**
 * Describes possible senders and receivers of JMS messages.
 */
public enum JmsCommunicatingService {
    NETWORK_MANAGER,
    YUKON_EIM,
    YUKON_SERVICE_MANAGER,
    YUKON_SIMULATORS,
    YUKON_WEBSERVER,
    YUKON_WEBSERVER_DEV_PAGES,
    YUKON_MESSAGE_BROKER,
    YUKON_WATCHDOG;
    ;
}
