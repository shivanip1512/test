package com.cannontech.common.rfn.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.model.NmCommunicationException;
/**
 * This service ask NM for the information about device.
 * RfnMetadata contains all device information.
 *
 */
public interface RfnDeviceMetadataMultiService {

    /**
     * Attempts to send a request for meta-data for rf devices.
     * Waits for response.
     * 
     * @throws NmCommunicationException if there was communication error or if NM returned an error
     */
    Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException;
    /**
     * Attempts to send a request for meta-data for rf device.
     * Waits for response.
     * 
     * @throws NmCommunicationException if there was communication error or if NM returned an error
     */

    Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(RfnIdentifier identifier,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException;
    
    /**
     * Attempts to send a request for meta-data for rf devices by gateways.
     * Waits for response.
     * 
     * @throws NmCommunicationException if there was communication error or if NM returned an error
     */
    Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForGatewayRfnIdentifiers(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException;

}