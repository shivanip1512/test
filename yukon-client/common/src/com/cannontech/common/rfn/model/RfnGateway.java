package com.cannontech.common.rfn.model;

import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.Locatable;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.util.NaturalOrderComparator;

public class RfnGateway extends RfnDevice implements Locatable, Comparable<RfnGateway> {
    private static final Logger log = YukonLogManager.getLogger(RfnGateway.class);
    
    private RfnGatewayData data;
    private PaoLocation paoLocation;
    private String upgradeVersion;
    private int id;
    
    public RfnGateway(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier);
        this.data = data;
    }
    
    public RfnGatewayData getData() {
        return data;
    }
    
    public void setUpgradeVersion(String version) {
        upgradeVersion = version;
    }
    
    public String getUpgradeVersion() {
        return upgradeVersion;
    }
    
    public boolean isUpgradeAvailable () {
        log.debug("Checking if upgrade is valid for " + getName());
        
        //If there's no gateway data, we can't check
        if (data == null) {
            return false;
        }
        
        //If the current or available version is null, we can't upgrade
        String currentVersionString = data.getReleaseVersion();
        log.debug("Current version: " + currentVersionString + ", Upgrade version: " + upgradeVersion);
        if (currentVersionString == null || upgradeVersion == null) {
            log.debug("Upgrade not valid due to null version.");
            return false;
        }
        
        try {
            // If the current version is less than the version that supports automated upgrades, then the upgrade for 
            // this gateway has to be done manually.
            GatewayFirmwareVersion currentVersion = GatewayFirmwareVersion.parse(currentVersionString);
            // Supported version varies based on gateway type.
            GatewayFirmwareVersion minimumSupportedFirmwareVersion = GatewayFirmwareVersion
                    .getMinimumUpgradeVersion(getPaoIdentifier().getPaoType());
            if (minimumSupportedFirmwareVersion == null) {
                log.error("Unsupported gateway PaoType: " + getPaoIdentifier().getPaoType());
                return false;
            }
            if (currentVersion.compareTo(minimumSupportedFirmwareVersion) < 0) {
                log.debug("Current version isn't above the minimum for " + getPaoIdentifier().getPaoType());
                return false;
            }
            //The available version has to be greater than or equal to the current version to allow upgrade
            GatewayFirmwareVersion availableVersion = GatewayFirmwareVersion.parse(upgradeVersion);
            boolean isUpgradeValid = currentVersion.compareTo(availableVersion) <= 0;
            log.debug("Upgrade valid: " + isUpgradeValid);
            return isUpgradeValid;
        } catch (Exception e) {
            log.error("Exception while testing firmware upgrade validity.", e);
            return false;
        }
    }
    
    public boolean isIpv6Supported() {
        if (data == null) {
            return false;
        }
        String currentVersionString = data.getReleaseVersion();
        GatewayFirmwareVersion currentVersion = GatewayFirmwareVersion.parse(currentVersionString);
        return currentVersion.compareTo(new GatewayFirmwareVersion(9, 0, 0)) >= 0;
    }
    
    public void setData(RfnGatewayData data) {
        this.data = data;
    }
    
    public double getTotalCompletionPercentage() {
        if (data == null) {
            return 0.0;
        }
        
        Set<DataSequence> sequences = data.getSequences();
        int numberOfSequences = sequences.size();
        
        if (numberOfSequences == 0) {
            return 0.0;
        }
        
        double sumOfCompletionPercentages = 0.0;
        
        for (DataSequence sequence : sequences) {
            sumOfCompletionPercentages += sequence.getCompletionPercentage();
        }
        return sumOfCompletionPercentages / numberOfSequences;
    }
    
    public boolean isTotalCompletionLevelWarning() {
        double totalCompletion = getTotalCompletionPercentage();
        return totalCompletion < 90 && totalCompletion >= 75;
    }
    
    public boolean isTotalCompletionLevelDanger() {
        double totalCompletion = getTotalCompletionPercentage();
        return totalCompletion < 75;
    }
    
    public boolean isLastCommFailed() {
        if (data == null) {
            return false;
        }
        return data.getLastCommStatus() == LastCommStatus.FAILED;
    }
    
    public boolean isLastCommMissed() {
        if (data == null) {
            return false;
        }
        return data.getLastCommStatus() == LastCommStatus.MISSED;
    }
    
    public boolean isLastCommUnknown() {
        if (data == null) {
            return false;
        }
        return data.getLastCommStatus() == LastCommStatus.UNKNOWN;
    }
    
    public boolean isAppModeNonNormal() {
        if (data == null) {
            return false;
        }
        return data.getMode() != AppMode.NORMAL;
    }
    
    @Override
    public PaoLocation getLocation() {
        return paoLocation;
    }
    
    public void setLocation(PaoLocation paoLocation) {
        this.paoLocation = paoLocation;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        id = getPaoIdentifier().getPaoId();
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((paoLocation == null) ? 0 : paoLocation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RfnGateway other = (RfnGateway) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (paoLocation == null) {
            if (other.paoLocation != null) {
                return false;
            }
        } else if (!paoLocation.equals(other.paoLocation)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(RfnGateway o) {
        return NaturalOrderComparator.naturalCompare(name, o.getName());
    }
    
    public RfnGateway withUpdateServer(GatewayUpdateModel updateServer) {

        RfnGatewayData.Builder builder = new RfnGatewayData.Builder();

        RfnGatewayData dataCopy = builder.copyOf(data)
            .updateServerUrl(updateServer.getUpdateServerUrl())
            .updateServerLogin(updateServer.getUpdateServerLogin())
            .build();

        setData(dataCopy);

        return this;
    }

    public boolean isDataStreamingSupported() {
        return false;
    }

    public String getNameWithIPAddress() {
        String ipAddress = Optional.ofNullable(data)
                                   .map(RfnGatewayData::getIpAddress)
                                   .map(ipa -> " (" + ipa + ")")
                                   .orElse("");
        return name + ipAddress;
    }

}