package com.cannontech.common.rfn.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.GatewayCertificateException;
import com.cannontech.common.rfn.model.RfnGateway;

public interface RfnGatewayCertificateUpdateService {
    /**
     * Send update package to RF gateways through Network Manager.
     * 
     * @param rfnGateways gateways to update.
     * @param certificatePackage the file containing the gateway certificate.
     * @throws IOException If there is an error reading the specified certificate file.
     * @throws GatewayCertificateException If there is an error parsing the specified certificate file.
     */
    public String sendUpdate(Set<RfnGateway> rfnGateways, MultipartFile certificatePackage) 
            throws IOException, GatewayCertificateException;

    /**
     * Send certificate update package to all RF gateways known to Network Manager.
     * 
     * @param certificatePackage the file containing the gateway certificate upgrade.
     * @return 
     * @throws IOException If there is an error reading the specified certificate file.
     * @throws GatewayCertificateException If there is an error parsing the specified certificate file.
     */
    public String sendUpdateAll(MultipartFile certificatePackage) throws IOException, GatewayCertificateException;

    /**
     * Reads the upgradeId from a given gateway upgrade package file and returns it.
     * @param certificatePackage gateway certificate upgrade package file.
     * @return upgradeId
     * @throws GatewayCertificateException if the format of the gateway certificate package file is invalid or the
     * upgradeId cannot be found.
     */
    public String getCertificateId(MultipartFile certificatePackage) throws GatewayCertificateException;
    
    /**
     * Retrieve a CertificateUpdate for display.
     */
    CertificateUpdate getCertificateUpdate(int updateId);

    /**
     * Retrieve all CertificateUpdates for display.
     */
    List<CertificateUpdate> getAllCertificateUpdates();
}
