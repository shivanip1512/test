package com.cannontech.common.rfn.message.gateway;

/**
 * Represents the possible results of a request(RfnUpdateServerAvailableVersionRequest) from Yukon to NM to
 * get the available version for all the update servers.
 * Used by {@link RfnUpdateServerAvailableVersionResponse}
 */
public enum RfnUpdateServerAvailableVersionResult {
    SUCCESS, INVALID_URL, RELEASE_VERSION_NOT_FOUND, NETWORK_ERROR, FAILED, ;
}
