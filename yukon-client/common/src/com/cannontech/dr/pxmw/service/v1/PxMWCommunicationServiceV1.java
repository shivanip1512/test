package com.cannontech.dr.pxmw.service.v1;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;

/**
 * This is the starting point for the service layer for the Px Middleware IT Project
 */

public interface PxMWCommunicationServiceV1 {

    /**
     * Returns site info with the list of devices
     * 
     * @param siteGuid - Represents the site id in the form of an 8-4-4-4-12 Uuid string.
     * @param recursive - Indicates whether the direct child devices and their children devices should all be returned for this site. (Optional)
     * @param includeDetail - Indicates whether to return devices detail information from their device profiles. (Optional)
     * @return site info containing device details
     * @throws PxMWCommunicationExceptionV1 
     */
    PxMWSiteV1 getSiteDevices(String siteGuid, Boolean recursive, Boolean includeDetail) throws PxMWCommunicationExceptionV1, PxMWException;

    /**
     * Sends message to SM to get a token, SM will send a message to PX get the token or return a cached token.
     */
    PxMWTokenV1 getToken() throws PxMWCommunicationExceptionV1, PxMWException;

    /**
     * Used by Simulator to clear token cache in SM
     */
    void clearCache() throws PxMWException;

    /**
     * @param token
     * @param deviceList - List of TimeSeries devices that you want results for (required)
     * @param range  - Range for the Start and End time period you want data on (required)
     * @return TimeSeries data for a set of devices over an interval
     * @throws PxMWCommunicationExceptionV1
     * 
     *         400 Invalid input parameter / Bad Request
     *         401 User is not authorized to send the command
     */
    PxMWTimeSeriesDataResponseV1 getTimeSeriesValues(List<PxMWTimeSeriesDeviceV1> deviceList, Range<Instant> range);
      /**
     * Sends a command to a device.
     * 
     * Meaning return errors statuses
     * 
     * 400 Invalid input parameter
     * 401 User is not authorized to send the command
     * 404 Device or device profile not defined    
     * 501 Device is not configured for IoT Hub or is not direct_method capable
     * 503 Error communicating with the device
     * 
     * PxMWCommunicationExceptionV1 contains the status
     */
    void sendCommand(String deviceGuid, String commandGuid, PxMWCommandRequestV1 request) throws PxMWCommunicationExceptionV1, PxMWException;
}
