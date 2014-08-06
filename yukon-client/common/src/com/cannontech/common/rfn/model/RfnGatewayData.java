package com.cannontech.common.rfn.model;

import java.util.Set;

import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.ConnectionType;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.common.rfn.message.gateway.Radio;

public class RfnGatewayData {
    //Based on paoName
    private String name;
    
    //Based on GatewayDataResponse
    private String hardwareVersion;
    private String softwareVersion;
    private String upperStackVersion;
    private String radioVersion;
    private String releaseVersion;
    private Set<ConflictType> versionConflicts;
    private AppMode mode;
    private ConnectionType connectionType;
    private String ipAddress;
    private String port;
    private ConnectionStatus connectionStatus;
    private LastCommStatus lastCommStatus;
    private long lastCommStatusTimestamp;
    private Set<Radio> radios;
    private Authentication authentication;
    private String collectionSchedule; // Cron string
    private Set<DataSequence> sequences;
    
    public RfnGatewayData(GatewayDataResponse dataResponse) {
        this.name = dataResponse.getRfnIdentifier().getSensorSerialNumber();
        
        this.hardwareVersion = dataResponse.getHardwareVersion();
        this.softwareVersion = dataResponse.getSoftwareVersion();
        this.upperStackVersion = dataResponse.getUpperStackVersion();
        this.radioVersion = dataResponse.getRadioVersion();
        this.releaseVersion = dataResponse.getReleaseVersion();
        this.versionConflicts = dataResponse.getVersionConflicts(); //copy?
        this.mode = dataResponse.getMode();
        this.connectionType = dataResponse.getConnectionType();
        this.ipAddress = dataResponse.getIpAddress();
        this.port = dataResponse.getPort();
        this.connectionStatus = dataResponse.getConnectionStatus();
        this.lastCommStatus = dataResponse.getLastCommStatus();
        this.lastCommStatusTimestamp = dataResponse.getLastCommStatusTimestamp();
        this.radios = dataResponse.getRadios(); //copy?
        this.authentication = dataResponse.getAuthentication();
        this.collectionSchedule = dataResponse.getCollectionSchedule();
        this.sequences = dataResponse.getSequences(); //copy?
    }

    public String getName() {
        return name;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public String getUpperStackVersion() {
        return upperStackVersion;
    }

    public String getRadioVersion() {
        return radioVersion;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public Set<ConflictType> getVersionConflicts() {
        return versionConflicts;
    }

    public AppMode getMode() {
        return mode;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public LastCommStatus getLastCommStatus() {
        return lastCommStatus;
    }

    public long getLastCommStatusTimestamp() {
        return lastCommStatusTimestamp;
    }

    public Set<Radio> getRadios() {
        return radios;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public String getCollectionSchedule() {
        return collectionSchedule;
    }

    public Set<DataSequence> getSequences() {
        return sequences;
    }
}
