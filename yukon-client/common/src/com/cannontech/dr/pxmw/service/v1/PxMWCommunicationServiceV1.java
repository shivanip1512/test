package com.cannontech.dr.pxmw.service.v1;

import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceChannelDetailsV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;

/**
 * This is the starting point for the service layer for the Px Middleware IT Project
 */

public interface PxMWCommunicationServiceV1 {

    /**
     * Gets a device profile. The device profile standard channel information is not merged with master channel information.
     * @throws PxMWCommunicationExceptionV1 
     * 
     */
    PxMWDeviceProfileV1 getDeviceProfile(String profileGuid) throws PxMWCommunicationExceptionV1, PxMWException;

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
    PxMWSiteV1 getSite(String siteGuid, Boolean recursive, Boolean includeDetail) throws PxMWCommunicationExceptionV1, PxMWException;

    /**
     * Returns the latest readings for a device channels.
     */
    PxMWDeviceChannelDetailsV1 getDeviceChannelDetails(String deviceGuid) throws PxMWCommunicationExceptionV1, PxMWException;

    /**
     * Sends message to SM to get a token, SM will send a message to PX get the token or return a cached token.
     */
    PxMWTokenV1 getToken() throws PxMWCommunicationExceptionV1, PxMWException;

}
