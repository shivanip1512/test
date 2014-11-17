package com.cannontech.common.rfn.message.gateway;

/**
 * Represents the possible results of a request from Yukon to NM to create, edit, or delete a gateway.
 * Used by {@link GatewayUpdateResponse}
 * 
 * Yukon will be responsible for detecting invalid gateway creation or edit data like 
 * blank passwords or invalid ip addresses. Note: Duplicate ip addresses or hostnames are valid.
 * 
 * This enum should only represent what NM is expected to handle.
 */
public enum GatewayUpdateResult {
    
    SUCCESSFUL,
    FAILED_UNKNOWN_GATEWAY, // delete or edit failure
    FAILED_INCORRECT_AUTH,  // create or edit failure
    FAILED_BAD_IP,          // create or edit failure
    FAILED,                 // all other failures
    ;
    
}