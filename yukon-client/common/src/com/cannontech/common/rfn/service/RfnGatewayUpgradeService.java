package com.cannontech.common.rfn.service;

import java.io.File;
import java.util.Set;

import com.cannontech.common.pao.YukonPao;

public interface RfnGatewayUpgradeService {
    /**
     * Send upgrade package to RF gateways through Network Manager.
     * 
     * @param gwPaos PaoIdentifiers for the gateways to upgrade.
     * @param upgradeID unique identifier for the upgrade package. An upgrade package file already
     *            has its uniqueID baked into it. The user will need to specify what that uniqueID
     *            is for the upgrade package they are applying.
     * @param upgradePackage the file containing the gateway upgrade.
     * @param callback caller will be notified of NM's response to the gateway upgrade request or
     *            any errors through this callback.
     */
    public void sendUpgrade(Set<? extends YukonPao> gwPaos, String upgradeID, File upgradePackage,
                            final RfnGatewayUpgradeCallback callback);
    
    /**
     * Send upgrade package to all RF gateways known to Network Manager.
     * 
     * @param upgradeID unique identifier for the upgrade package. An upgrade package file already
     *            has its uniqueID baked into it. The user will need to specify what that uniqueID
     *            is for the upgrade package they are applying.
     * @param upgradePackage the file containing the gateway upgrade.
     * @param callback caller will be notified of NM's response to the gateway upgrade request or
     *            any errors through this callback.
     */
    public void sendUpgradeAll(String upgradeID, File upgradePackage,
                               final RfnGatewayUpgradeCallback callback);
}
