package com.cannontech.common.rfn.message.gateway;

/**
 * Represents the possible results of a request from Yukon to NM for RfnUpdateServerAvailableVersionRequest
 * Used by {@link RfnUpdateServerAvailableVersionResponse}
 */
public enum RfnUpdateServerAvailableVersionResult {
    SUCCESS,
    INVALID_URL,
    RELEASE_VERSION_NOT_FOUND,
    NETWORK_ERROR,
    FAILED, ;
}
