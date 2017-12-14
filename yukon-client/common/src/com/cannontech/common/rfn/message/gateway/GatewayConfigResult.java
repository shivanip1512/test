package com.cannontech.common.rfn.message.gateway;
public enum GatewayConfigResult {
    
    SUCCESSFUL,
    FAILED_UNKNOWN_GATEWAY,     // unknown rfn ID, can't find gateway
    FAILED_DUPLICATE_CONFIG,    // if config item must be unique per gateway
    FAILED_INVALID_VALUE,       // config value is of invalid format or range
    FAILED_NOT_SUPPORTED,       // config item not supported by gateway
    FAILED,                     // all other failures
    ;
    
}