package com.cannontech.common.rfn.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.rfn.model.GatewayCertificateUpdateInfo;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;

/**
 * This dao maintains the history of rfn gateway certificate update attempts. Each update uses a single certificate 
 * and can target one or many gateways. The status of each gateway in the update process is tracked individually.
 */
public interface GatewayCertificateUpdateDao {
    
    /**
     * Create a new certificate update.
     * @return The id of this certificate update.
     */
    int createUpdate(String certificateId, String fileName);

    /**
     * Create one or more gateway-specific entries within a certificate update. Each entry is initialized to
     * the specified state.
     */
    void createEntries(int updateId, GatewayCertificateUpdateStatus status, Collection<Integer> gatewayIds);
    
    /**
     * Update the status of a gateway within a certificate update.
     */
    void updateEntry(int updateId, int gatewayId, GatewayCertificateUpdateStatus status);
    
    /**
     * Retrieve the id of the certificate update with the most recent SendDate and the specified certificateId;
     */
    int getLatestUpdateForCertificate(String certificateId);
    
    /**
     * Retrieve info on a certificate update attempt (which may involve multiple gateways).
     */
    GatewayCertificateUpdateInfo getUpdateInfo(int updateId);
    
    /**
     * Retrieve info for all certificate  attempts, sorted from most recent to least recent.
     */
    List<GatewayCertificateUpdateInfo> getAllUpdateInfo();
}
