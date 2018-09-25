package com.cannontech.dr.pxwhite.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.dr.pxwhite.model.PxWhiteDeviceChannels;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceData;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceTimeSeriesData;
import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;

/**
 * This service provides communications to the PX White REST API.
 */
public interface PxWhiteCommunicationService {
    
    /**
     * Request a security token, which can be used for authentication when making other requests.
     * @param applicationId Id of the PX White application to authenticate against, in the form 
     * aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee.
     * @return An alphanumeric token String, which will expire in one hour.
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     */
    public String getSecurityToken(String user, String password, String applicationId) throws PxWhiteCommunicationException;
    
    /**
     * Refresh an expired security token.
     * @param expiredToken The most recent expired token. The token can be renewed within two hours of expiration.
     * @return An alphanumeric token String, which will expire in one hour.
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     */
    public String refreshSecurityToken(String user, String expiredToken) throws PxWhiteCommunicationException;
    
    /**
     * Get detailed information about a security token.
     * @param token A valid PX White security token.
     * @return Detailed information about the token.
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     */
    public TokenDetails getTokenDetails(String token) throws PxWhiteCommunicationException;
    
    /**
     * Get the current values for a set of tags on a device. Tags are data channel identifiers in PX White.
     */
    public PxWhiteDeviceData getDeviceDataCurrentValues(String token, String deviceId, List<String> tags) throws PxWhiteCommunicationException;
    
    /**
     * Get time series data for a set of tags on a device. These tags are PX White data channel identifiers,
     * combined with a "trait" marker in the form "channel.trait". The valid traits for a regular non-array channel are 
     * "v", "avg", "min", or "max". The "v" represents the actual value captured on the timestamp. If the .trait part 
     * is missing, the default trait "v" is implied.
     * The query range is not allowed to exceed 6 months, and no more than 4 tags can be specified, for performance.
     */
    public PxWhiteDeviceTimeSeriesData getDeviceDataByDateRange(String token, String deviceId, List<String> tags, Instant startDate, Instant endDate) throws PxWhiteCommunicationException;
    
    /**
     * Get data channel information for a device.
     */
    public PxWhiteDeviceChannels getChannels(String token, String deviceId) throws PxWhiteCommunicationException;
}
