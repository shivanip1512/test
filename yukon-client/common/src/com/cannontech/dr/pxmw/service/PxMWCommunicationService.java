package com.cannontech.dr.pxmw.service;

import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;

/**
 * This is the starting point for the service layer for the Px Middleware IT Project
 */

public interface PxMWCommunicationService {

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

}
