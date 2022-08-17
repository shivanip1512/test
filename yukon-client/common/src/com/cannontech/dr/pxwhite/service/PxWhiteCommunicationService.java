package com.cannontech.dr.pxwhite.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.dr.pxwhite.model.PxWhiteDeviceChannels;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceData;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceTimeSeriesData;
import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;

/**
 * This service provides low-level communications to the PX White REST API. It is intended to be stateless, so it does 
 * not track authentication info or retain any data across method calls. It sends requests, and de-serializes the 
 * responses into Java objects.
 * <p>
 * Swagger documentation of PX White endpoints can be viewed at https://adopteriotwebapi.eaton.com/swagger/ui/index
 * (requires a PX White account)
 */
public interface PxWhiteCommunicationService {
    
    /**
     * Request a security token, which can be used for authentication when making other requests.
     * 
     * @param user The user's registered email address.
     * @param password The user's registered password.
     * @param applicationId Id of the PX White application to authenticate against, in the UUID form 
     * aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12).
     * 
     * @return An alphanumeric token String, which will expire in one hour.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code. Error
     * code 401 = Unauthorized. Error code 403 = Account Locked.
     */
    public String getSecurityToken(String user, String password, String applicationId) throws PxWhiteCommunicationException;
    
    /**
     * Refresh an expired security token.
     * 
     * @param user The user's registered email address.
     * @param expiredToken The most recent expired token. The token can be renewed within two hours of expiration.
     * 
     * @return An alphanumeric token String, which will expire in one hour.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     */
    public String refreshSecurityToken(String user, String expiredToken) throws PxWhiteCommunicationException;
    
    /**
     * Get detailed information about a security token.
     * 
     * @param token A valid PX White security token.
     * 
     * @return Detailed information about the token.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     */
    public TokenDetails getTokenDetails(String token) throws PxWhiteCommunicationException;
    
    /**
     * Get the current values for a set of tags on a device. Tags are data channel identifiers in PX White.
     * 
     * @param token A valid PX White authentication token.
     * @param deviceId A PX White device identifier, in the UUID form aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12).
     * @param tags A list of tags identifying data channels on the device.
     * 
     * @return A data object containing latest data values for the specified channels and device.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     * 
     * @see getChannels getChannels method - for info on the channels supported by a device, including the channel tag
     */
    public PxWhiteDeviceData getDeviceDataCurrentValues(String token, String deviceId, List<String> tags) throws PxWhiteCommunicationException;
    
    /**
     * Get time series data for a set of channels on a device.
     * 
     * @param token A valid PX White security token.
     * @param deviceId A PX White device identifier, in the UUID form aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12).
     * @param tags A list of Strings identifying the data to retrieve. No more than 4 tags can be specified in a single
     * request. These strings are PX White data channel tags combined with a "trait" marker, in the form "tag.trait". 
     * The valid traits for a regular non-array channel are "v", "avg", "min", or "max". The "v" represents the actual 
     * value captured on the timestamp. If the trait part is missing, the default trait "v" is implied.
     * @param startDate The start of the date range for data retrieval.
     * @param endDate The end of the date range for data retrieval. The query range is not allowed to exceed 6 months.
     * 
     * @return Time series data for the specified device and channel tags
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     * 
     * @see getChannels getChannels method - for info on the channels supported by a device, including the channel tag
     */
    public PxWhiteDeviceTimeSeriesData getDeviceDataByDateRange(String token, String deviceId, List<String> tags, Instant startDate, Instant endDate) throws PxWhiteCommunicationException;
    
    /**
     * Get data channel information for a device. This includes the "tag", which is typically how channels are 
     * identified in requests, as well as meta-data describing the attributes of the data in the channel.
     * 
     * @param token A valid PX White security token.
     * @param deviceId A PX White device identifier, in the UUID form aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12).
     * 
     * @return An object containing information about the channels supported by the specified device.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code. 
     */
    public PxWhiteDeviceChannels getChannels(String token, String deviceId) throws PxWhiteCommunicationException;
    
    /**
     * Send a command to the specified channel on a particular device. The channel must be writable.
     * 
     * @param token A valid PX White security token.
     * @param deviceId A PX White device identifier, in the UUID form aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12).
     * @param tag A tag identifying the data channel to write to.
     * 
     * @return true if the command was sent successfully.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code. 
     * 
     * @see getChannels getChannels method - for info on the channels supported by a device, including the channel tag 
     * and whether it's writable.
     */
    public boolean sendCommand(String token, String deviceId, String tag, String command) throws PxWhiteCommunicationException;
}
