package com.cannontech.common.rfn.service;

import java.io.File;
import java.util.Set;

import com.cannontech.common.rfn.model.GatewayCertificateException;
import com.cannontech.common.rfn.model.RfnGateway;

public interface RfnGatewayUpgradeService {
    /**
     * Send upgrade package to RF gateways through Network Manager.
     * 
     * @param rfnGateways gateways to upgrade.
     * @param upgradePackage the file containing the gateway upgrade.
     * @param callback caller will be notified of NM's response to the gateway upgrade request or
     *            any errors through this callback.
     */
    public void sendUpgrade(Set<RfnGateway> rfnGateways, File upgradePackage,
                            final RfnGatewayUpgradeCallback callback);

    /**
     * Send upgrade package to all RF gateways known to Network Manager.
     * 
     * @param upgradePackage the file containing the gateway upgrade.
     * @param callback caller will be notified of NM's response to the gateway upgrade request or
     *            any errors through this callback.
     */
    public void sendUpgradeAll(File upgradePackage,
                               final RfnGatewayUpgradeCallback callback);

    /**
     * Reads the upgradeId from a given gateway upgrade package file and returns it.
     * @param upgradePackage gateway upgrade package file.
     * @return upgradeId
     * @throws GatewayCertificateException if the format of the gateway upgrade package file is
     *             invalid or the upgradeId cannot be found.
     */
    public String getUpgradeId(File upgradePackage) throws GatewayCertificateException;
}
