package com.cannontech.dr.pxmw.service;

import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;

/**
 * This is the starting point for the service layer for the Px Middleware IT Project
 */

public interface PxMWCommunicationServiceV1 {

    /**
     * Request a security token, which can be used for authentication when making other requests.
     * 
     * @param user The user's registered email address.
     * @param password The user's registered password.
     * @param applicationId Id of the PX White application to authenticate against, in the Guid form 
     * aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12).
     * 
     * @return An alphanumeric token String, which will expire in one hour.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code. Error
     * code 401 = Unauthorized. Error code 403 = Account Locked.
     */
    String getSecurityToken(String user, String password, String applicationId) throws PxWhiteCommunicationException;

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
    String refreshSecurityToken(String user, String expiredToken);

    /**
     * Get detailed information about a security token.
     * 
     * @param token A valid PX White security token.
     * 
     * @return Detailed information about the token.
     * 
     * @throws PxWhiteCommunicationException if an error occurs or the response contains a bad http status code.
     */
    TokenDetails getTokenDetails(String token);

    /**
     * Gets a device profile. The device profile standard channel information is not merged with master channel information.
     * @throws PxMWCommunicationExceptionV1 
     * 
     */
    PxMWDeviceProfileV1 getDeviceProfile(String token, String profileGuid) throws PxMWCommunicationExceptionV1;

    /**
     * Returns site info with the list of devices
     * 
     * @param token
     * @param siteGuid - Represents the site id in the form of an 8-4-4-4-12 Uuid string.
     * @param recursive - Indicates whether the direct child devices and their children devices should all be returned for this site. (Optional)
     * @param includeDetail - Indicates whether to return devices detail information from their device profiles. (Optional)
     * @return site info containing device details
     * @throws PxMWCommunicationExceptionV1 
     */
    PxMWSiteV1 getSite(String token, String siteGuid, Boolean recursive, Boolean includeDetail) throws PxMWCommunicationExceptionV1;

}
