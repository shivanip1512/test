package com.cannontech.common.rfn.service;

import java.io.File;
import java.util.Set;

import com.cannontech.common.rfn.model.GatewayCertificateException;
import com.cannontech.common.rfn.model.RfnGateway;

public interface RfnGatewayUpdateService {
    /**
     * Send update package to RF gateways through Network Manager.
     * 
     * @param rfnGateways gateways to update.
     * @param certificatePackage the file containing the gateway certificate.
     * @param callback caller will be notified of NM's response to the gateway certificate upgrade request or
     * any errors through this callback.
     */
    public void sendUpdate(Set<RfnGateway> rfnGateways, File certificatePackage,
                           final RfnGatewayUpdateCallback callback);

    /**
     * Send certificate update package to all RF gateways known to Network Manager.
     * 
     * @param certificatePackage the file containing the gateway certificate upgrade.
     * @param callback caller will be notified of NM's response to the gateway certificate upgrade request or
     * any errors through this callback.
     */
    public void sendUpgradeAll(File certificatePackage, final RfnGatewayUpdateCallback callback);

    /**
     * Reads the upgradeId from a given gateway upgrade package file and returns it.
     * @param certificatePackage gateway certificate upgrade package file.
     * @return upgradeId
     * @throws GatewayCertificateException if the format of the gateway certificate package file is invalid or the
     * upgradeId cannot be found.
     */
    public String getCertificateId(File certificatePackage) throws GatewayCertificateException;
}
