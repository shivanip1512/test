package com.cannontech.dr.eatonCloud.service.v1;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.eatonCloud.model.EatonCloudException;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudDeviceDetailV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteDevicesV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;

/**
 * This is the starting point for the service layer for the Eaton Cloud Px Middleware IT Project
 */

public interface EatonCloudCommunicationServiceV1 {


    /**
     * Returns site info with the list of devices
     * 
     * @param siteGuid      - Represents the site id in the form of an 8-4-4-4-12 Uuid string.
     * @param recursive     - Indicates whether the direct child devices and their children devices should all be returned for
     *                      this site. (Optional)
     * @param includeDetail - Indicates whether to return devices detail information from their device profiles. (Optional)
     * @return site info containing device details
     * @throws EatonCloudCommunicationExceptionV1
     * @throws EatonCloudException
     * 
     * 200 OK
     * 400 Bad Request
     * 401 Unauthorized
     * 404 Not Found
     */
    EatonCloudSiteDevicesV1 getSiteDevices(String siteGuid, Boolean recursive, Boolean includeDetail) throws EatonCloudCommunicationExceptionV1, EatonCloudException;

    /**
     * Sends message to SM to get a token, SM will send a message to PX get the token or return a cached token.
     */
    EatonCloudTokenV1 getToken() throws EatonCloudCommunicationExceptionV1, EatonCloudException;

    /**
     * Used by Simulator to clear token cache in SM
     */
    void clearCache() throws EatonCloudException;

    /**
     * @param token
     * @param deviceList - List of TimeSeries devices that you want results for (required)
     * @param range  - Range for the Start and End time period you want data on (required)
     * @return TimeSeries data for a set of devices over an interval
     *         
     * @throws EatonCloudCommunicationExceptionV1
     * @throws EatonCloudException
     * 
     * 200 OK
     * 400 Bad Request
     * 401 Unauthorized
     */
    List<EatonCloudTimeSeriesDeviceResultV1> getTimeSeriesValues(List<EatonCloudTimeSeriesDeviceV1> deviceList, Range<Instant> range);
      /**
     * Sends a command to a device.
     * 
     * EatonCloudCommunicationExceptionV1 contains the status
     * @throws EatonCloudCommunicationExceptionV1
     * @throws EatonCloudException
     * 
     * 200 OK
     * 400 Bad Request
     * 401 Unauthorized
     * 404 Not Found
     */
    void sendCommand(String deviceGuid, EatonCloudCommandRequestV1 request) throws EatonCloudCommunicationExceptionV1, EatonCloudException;

    
    /**
     * Retrieves the device information
     * 
     * @param deviceGuid - Device id in the form of uuid
     * @param recursive
     * @return device details
     * @throws EatonCloudCommunicationExceptionV1
     * @throws EatonCloudException
     * 
     * 200 OK
     * 400 Bad Request
     * 401 Unauthorized
     * 404 Not Found
     */
    EatonCloudDeviceDetailV1 getDeviceDetails(String deviceGuid, Boolean recursive) throws EatonCloudCommunicationExceptionV1, EatonCloudException;

    /**
     * Retrieves the list of sites
     */
    List<EatonCloudSiteV1> getSites(String siteGuid) throws EatonCloudCommunicationExceptionV1, EatonCloudException;
}
