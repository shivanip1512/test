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

public final class RfnGatewayData {
    //Based on paoName
    private final String name;
    
    //Based on GatewayDataResponse
    private final String hardwareVersion;
    private final String softwareVersion;
    private final String upperStackVersion;
    private final String radioVersion;
    private final String releaseVersion;
    private final Set<ConflictType> versionConflicts;
    private final AppMode mode;
    private final ConnectionType connectionType;
    private final String ipAddress;
    private final String port;
    private final ConnectionStatus connectionStatus;
    private final LastCommStatus lastCommStatus;
    private final long lastCommStatusTimestamp;
    private final Set<Radio> radios;
    private final Authentication admin;
    private final Authentication superAdmin;
    private final String collectionSchedule; // Cron string
    private final Set<DataSequence> sequences;
    private final short routeColor;
    
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
        this.admin = dataResponse.getAdmin();
        this.superAdmin = dataResponse.getSuperAdmin();
        this.collectionSchedule = dataResponse.getCollectionSchedule();
        this.sequences = dataResponse.getSequences(); //copy?
        this.routeColor = dataResponse.getRouteColor();
    }
    
    //private constructor for builder
    private RfnGatewayData(String name, String hardwareVersion, String softwareVersion, String upperStackVersion, 
                           String radioVersion, String releaseVersion, Set<ConflictType> versionConflicts, 
                           AppMode mode, ConnectionType connectionType, String ipAddress, String port, 
                           ConnectionStatus connectionStatus, LastCommStatus lastCommStatus, 
                           long lastCommStatusTimestamp, Set<Radio> radios, Authentication user, Authentication admin,
                           Authentication superAdmin, String collectionSchedule, Set<DataSequence> sequences, 
                           short routeColor) {
        this.name = name;
        this.hardwareVersion = hardwareVersion;
        this.softwareVersion = softwareVersion;
        this.upperStackVersion = upperStackVersion;
        this.radioVersion = radioVersion;
        this.releaseVersion = releaseVersion;
        this.versionConflicts = versionConflicts;
        this.mode = mode;
        this.connectionType = connectionType;
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectionStatus = connectionStatus;
        this.lastCommStatus = lastCommStatus;
        this.lastCommStatusTimestamp = lastCommStatusTimestamp;
        this.radios = radios;
        this.admin = admin;
        this.superAdmin = superAdmin;
        this.collectionSchedule = collectionSchedule;
        this.sequences = sequences;
        this.routeColor = routeColor;
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

    public Authentication getAdmin() {
        return admin;
    }

    public Authentication getSuperAdmin() {
        return superAdmin;
    }

    public String getCollectionSchedule() {
        return collectionSchedule;
    }

    public Set<DataSequence> getSequences() {
        return sequences;
    }
    
    public short getRouteColor() {
        return routeColor;
    }
    
    public static class Builder {
        private String name;
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
        private Authentication user;
        private Authentication admin;
        private Authentication superAdmin;
        private String collectionSchedule;
        private Set<DataSequence> sequences;
        private short routeColor;
        
        public RfnGatewayData build() {
            return new RfnGatewayData(name, hardwareVersion, softwareVersion, upperStackVersion, radioVersion, 
                                      releaseVersion, versionConflicts, mode, connectionType, ipAddress, port, 
                                      connectionStatus, lastCommStatus, lastCommStatusTimestamp, radios, user, admin,
                                      superAdmin, collectionSchedule, sequences, routeColor);
            
        }
        
        public Builder copyOf(RfnGatewayData oldData) {
            this.name = oldData.getName();
            this.hardwareVersion = oldData.getHardwareVersion();
            this.softwareVersion = oldData.getSoftwareVersion();
            this.upperStackVersion = oldData.getUpperStackVersion();
            this.radioVersion = oldData.getRadioVersion();
            this.releaseVersion = oldData.getReleaseVersion();
            this.versionConflicts = oldData.getVersionConflicts();
            this.mode = oldData.getMode();
            this.connectionType = oldData.getConnectionType();
            this.ipAddress = oldData.getIpAddress();
            this.port = oldData.getPort();
            this.connectionStatus = oldData.getConnectionStatus();
            this.lastCommStatus = oldData.getLastCommStatus();
            this.lastCommStatusTimestamp = oldData.getLastCommStatusTimestamp();
            this.radios = oldData.getRadios();
            this.admin = oldData.getAdmin();
            this.superAdmin = oldData.getSuperAdmin();
            this.collectionSchedule = oldData.getCollectionSchedule();
            this.sequences = oldData.getSequences();
            this.routeColor = oldData.getRouteColor();
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder hardwareVersion(String hardwareVersion) {
            this.hardwareVersion = hardwareVersion;
            return this;
        }
        
        public Builder softwareVersion(String softwareVersion) {
            this.softwareVersion = softwareVersion;
            return this;
        }
        
        public Builder upperStackVersion(String upperStackVersion) {
            this.upperStackVersion = upperStackVersion;
            return this;
        }
        
        public Builder radioVersion(String radioVersion) {
            this.radioVersion = radioVersion;
            return this;
        }
        
        public Builder releaseVersion(String releaseVersion) {
            this.releaseVersion = releaseVersion;
            return this;
        }
        
        public Builder versionConflicts(Set<ConflictType> versionConflicts) {
            this.versionConflicts = versionConflicts;
            return this;
        }
        
        public Builder mode(AppMode mode) {
            this.mode = mode;
            return this;
        }
        
        public Builder connectionType(ConnectionType connectionType) {
            this.connectionType = connectionType;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder port(String port) {
            this.port = port;
            return this;
        }
        
        public Builder connectionStatus(ConnectionStatus connectionStatus) {
            this.connectionStatus = connectionStatus;
            return this;
        }
        
        public Builder lastCommStatus(LastCommStatus lastCommStatus) {
            this.lastCommStatus = lastCommStatus;
            return this;
        }
        
        public Builder lastCommStatusTimestamp(long lastCommStatusTimestamp) {
            this.lastCommStatusTimestamp = lastCommStatusTimestamp;
            return this;
        }
        
        public Builder radios(Set<Radio> radios) {
            this.radios = radios;
            return this;
        }
        
        public Builder admin(Authentication admin) {
            this.admin = admin;
            return this;
        }
        
        public Builder superAdmin(Authentication superAdmin) {
            this.superAdmin = superAdmin;
            return this;
        }
        
        public Builder collectionSchedule(String collectionSchedule) {
            this.collectionSchedule = collectionSchedule;
            return this;
        }
        
        public Builder sequences(Set<DataSequence> sequences) {
            this.sequences = sequences;
            return this;
        }
        
        public Builder routeColor(short routeColor) {
            this.routeColor = routeColor;
            return this;
        }
    }
    
}