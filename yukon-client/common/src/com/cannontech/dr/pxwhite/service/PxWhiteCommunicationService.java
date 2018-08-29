package com.cannontech.dr.pxwhite.service;

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
    
}
