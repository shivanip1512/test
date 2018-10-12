package com.cannontech.common.rfn.model;

import java.util.Optional;
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
    private final String updateServerUrl;
    private final Authentication updateServerLogin;
    private final double currentDataStreamingLoading;
    private final double maxDataStreamingLoading;
    private final int gwTotalNodes;
    private final int gwTotalReadyNodes;
    private final int gwTotalNotReadyNodes;
    private final int gwTotalNodesWithSN;
    private final int gwTotalNodesWithInfo;
    private final int gwTotalNodesNoInfo;
    private final String ipv6Prefix;
    private final String ipv6PrefixSuggested;
    
    public RfnGatewayData(GatewayDataResponse dataResponse, String name) {
        
        this.name = name;
        
        hardwareVersion = dataResponse.getHardwareVersion();
        softwareVersion = dataResponse.getSoftwareVersion();
        upperStackVersion = dataResponse.getUpperStackVersion();
        radioVersion = dataResponse.getRadioVersion();
        releaseVersion = dataResponse.getReleaseVersion();
        versionConflicts = dataResponse.getVersionConflicts(); //copy?
        mode = dataResponse.getMode();
        connectionType = dataResponse.getConnectionType();
        ipAddress = dataResponse.getIpAddress();
        port = dataResponse.getPort();
        connectionStatus = dataResponse.getConnectionStatus();
        lastCommStatus = dataResponse.getLastCommStatus();
        lastCommStatusTimestamp = dataResponse.getLastCommStatusTimestamp();
        radios = dataResponse.getRadios(); //copy?
        admin = dataResponse.getAdmin();
        superAdmin = dataResponse.getSuperAdmin();
        collectionSchedule = dataResponse.getCollectionSchedule();
        sequences = dataResponse.getSequences(); //copy?
        routeColor = dataResponse.getRouteColor();
        updateServerUrl = dataResponse.getUpdateServerUrl(); 
        updateServerLogin = dataResponse.getUpdateServerLogin();
        currentDataStreamingLoading = dataResponse.getCurrentDataStreamingLoading();
        maxDataStreamingLoading = dataResponse.getMaxDataStreamingCapacity();
        gwTotalNodes = dataResponse.getGwTotalNodes();
        gwTotalReadyNodes = dataResponse.getGwTotalReadyNodes();
        gwTotalNotReadyNodes = dataResponse.getGwTotalNotReadyNodes();
        gwTotalNodesWithSN = dataResponse.getGwTotalNodesWithSN();
        gwTotalNodesWithInfo = dataResponse.getGwTotalNodesWithInfo();
        gwTotalNodesNoInfo = dataResponse.getGwTotalNodesNoInfo();
        ipv6Prefix = dataResponse.getIpv6Prefix();
        ipv6PrefixSuggested = dataResponse.getIpv6PrefixSuggested();
    }
    
    /** Private constructor for builder */
    private RfnGatewayData(String name, String hardwareVersion, String softwareVersion, String upperStackVersion, 
                           String radioVersion, String releaseVersion, Set<ConflictType> versionConflicts, 
                           AppMode mode, ConnectionType connectionType, String ipAddress, String port, 
                           ConnectionStatus connectionStatus, LastCommStatus lastCommStatus, 
                           long lastCommStatusTimestamp, Set<Radio> radios, Authentication admin,
                           Authentication superAdmin, String collectionSchedule, Set<DataSequence> sequences, 
                           short routeColor, String updateServerUrl, Authentication updateServerLogin,
                           double currentDataStreamingLoading, double maxDataStreamingLoading, int gwTotalNodes,
                           int gwTotalReadyNodes, int gwTotalNotReadyNodes, int gwTotalNodesWithSN, 
                           int gwTotalNodesWithInfo, int gwTotalNodesNoInfo, String ipv6Prefix, String ipv6PrefixSuggested) {
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
        this.updateServerUrl = updateServerUrl;
        this.updateServerLogin = updateServerLogin;
        this.currentDataStreamingLoading = currentDataStreamingLoading;
        this.maxDataStreamingLoading = maxDataStreamingLoading;
        this.gwTotalNodes = gwTotalNodes;
        this.gwTotalReadyNodes = gwTotalReadyNodes;
        this.gwTotalNotReadyNodes = gwTotalNotReadyNodes;
        this.gwTotalNodesWithSN = gwTotalNodesWithSN;
        this.gwTotalNodesWithInfo = gwTotalNodesWithInfo;
        this.gwTotalNodesNoInfo = gwTotalNodesNoInfo;
        this.ipv6Prefix = ipv6Prefix;
        this.ipv6PrefixSuggested = ipv6PrefixSuggested;
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
    
    public String getUpdateServerUrl() {
        return updateServerUrl;
    }

    public Authentication getUpdateServerLogin() {
        return updateServerLogin;
    }

    public double getCurrentDataStreamingLoading() {
        return currentDataStreamingLoading;
    }

    public double getMaxDataStreamingLoading() {
        return maxDataStreamingLoading;
    }
    
    public double getDataStreamingLoadingPercent() {
        return (currentDataStreamingLoading / maxDataStreamingLoading) * 100;
    }

    public double getPercentForReadyNodes() {
        int total = gwTotalReadyNodes + gwTotalNotReadyNodes;
        if (total <= 0) {
            return 0.0;
        }
        return (double) (gwTotalReadyNodes * 100) / total;
    }

    public double getPercentForNodesWithInfo() {
        int total = gwTotalNodesWithInfo + gwTotalNodesNoInfo;
        if (total <= 0) {
            return 0.0;
        }
        return (double) (gwTotalNodesWithInfo * 100) / total;
    }

    public double getPercentForNodesWithSN() {
        return  (gwTotalNodesWithSN * 100) / gwTotalNodes;
    }

    public int getGwTotalNodes() {
        return gwTotalNodes;
    }
    
    public int getGwTotalReadyNodes() {
        return gwTotalReadyNodes;
    }

    public int getGwTotalNotReadyNodes() {
        return gwTotalNotReadyNodes;
    }
 
    public int getGwTotalNodesWithSN() {
        return gwTotalNodesWithSN;
    }
    
    public int getGwTotalNodesWithInfo() {
        return gwTotalNodesWithInfo;
    }

    public int getGwTotalNodesNoInfo() {
        return gwTotalNodesNoInfo;
    }

    public String getIpv6Prefix() {
        return ipv6Prefix;
    }

    public String getIpv6PrefixSuggested() {
        return ipv6PrefixSuggested;
    }
    
    public String getMacAddress() {
        Optional<Radio> mac =
            radios.stream()
                //FFFFFFFFFFFF is an erroneous radio MAC address reported by the gateway when the gateway application starts up before the radio has finished loading
                .filter(radio -> !radio.getMacAddress().isEmpty() && !radio.getMacAddress().equals("FFFFFFFFFF"))
                .findFirst();
        if (mac.isPresent()) {
            return mac.get().getMacAddress();
        }
        return null;
    }

    @Override
    public String toString() {
        return "RfnGatewayData [name=" + name + ", hardwareVersion=" + hardwareVersion + ", softwareVersion="
               + softwareVersion + ", upperStackVersion=" + upperStackVersion + ", radioVersion=" + radioVersion
               + ", releaseVersion=" + releaseVersion + ", versionConflicts=" + versionConflicts + ", mode=" + mode
               + ", connectionType=" + connectionType + ", ipAddress=" + ipAddress + ", port=" + port
               + ", connectionStatus=" + connectionStatus + ", lastCommStatus=" + lastCommStatus
               + ", lastCommStatusTimestamp=" + lastCommStatusTimestamp + ", radios=" + radios + ", admin=" + admin
               + ", superAdmin=" + superAdmin + ", collectionSchedule=" + collectionSchedule + ", sequences="
               + sequences + ", routeColor=" + routeColor + ", updateServerUrl=" + updateServerUrl
               + ", updateServerLogin=" + updateServerLogin + ", currentDataStreamingLoading="
               + currentDataStreamingLoading + ", maxDataStreamingLoading=" + maxDataStreamingLoading
               + ", gwTotalNodes=" + gwTotalNodes + ", gwTotalReadyNodes=" + gwTotalReadyNodes
               + ", gwTotalNotReadyNodes=" + gwTotalNotReadyNodes + ", gwTotalNodesWithSN=" + gwTotalNodesWithSN
               + ", gwTotalNodesWithInfo=" + gwTotalNodesWithInfo + ", gwTotalNodesNoInfo=" + gwTotalNodesNoInfo
               + ", ipv6Prefix=" + ipv6Prefix + ", ipv6PrefixSuggested=" + ipv6PrefixSuggested + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((admin == null) ? 0 : admin.hashCode());
        result = prime * result + ((collectionSchedule == null) ? 0 : collectionSchedule.hashCode());
        result = prime * result + ((connectionStatus == null) ? 0 : connectionStatus.hashCode());
        result = prime * result + ((connectionType == null) ? 0 : connectionType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(currentDataStreamingLoading);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + gwTotalNodes;
        result = prime * result + gwTotalNodesNoInfo;
        result = prime * result + gwTotalNodesWithInfo;
        result = prime * result + gwTotalNodesWithSN;
        result = prime * result + gwTotalNotReadyNodes;
        result = prime * result + gwTotalReadyNodes;
        result = prime * result + ((hardwareVersion == null) ? 0 : hardwareVersion.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((lastCommStatus == null) ? 0 : lastCommStatus.hashCode());
        result = prime * result + (int) (lastCommStatusTimestamp ^ (lastCommStatusTimestamp >>> 32));
        temp = Double.doubleToLongBits(maxDataStreamingLoading);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((radioVersion == null) ? 0 : radioVersion.hashCode());
        result = prime * result + ((radios == null) ? 0 : radios.hashCode());
        result = prime * result + ((releaseVersion == null) ? 0 : releaseVersion.hashCode());
        result = prime * result + routeColor;
        result = prime * result + ((sequences == null) ? 0 : sequences.hashCode());
        result = prime * result + ((softwareVersion == null) ? 0 : softwareVersion.hashCode());
        result = prime * result + ((superAdmin == null) ? 0 : superAdmin.hashCode());
        result = prime * result + ((updateServerLogin == null) ? 0 : updateServerLogin.hashCode());
        result = prime * result + ((updateServerUrl == null) ? 0 : updateServerUrl.hashCode());
        result = prime * result + ((upperStackVersion == null) ? 0 : upperStackVersion.hashCode());
        result = prime * result + ((versionConflicts == null) ? 0 : versionConflicts.hashCode());
        result = prime * result + ((ipv6Prefix == null) ? 0 : ipv6Prefix.hashCode());
        result = prime * result + ((ipv6PrefixSuggested == null) ? 0 : ipv6PrefixSuggested.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RfnGatewayData other = (RfnGatewayData) obj;
        if (admin == null) {
            if (other.admin != null) {
                return false;
            }
        } else if (!admin.equals(other.admin)) {
            return false;
        }
        if (collectionSchedule == null) {
            if (other.collectionSchedule != null) {
                return false;
            }
        } else if (!collectionSchedule.equals(other.collectionSchedule)) {
            return false;
        }
        if (connectionStatus != other.connectionStatus) {
            return false;
        }
        if (connectionType != other.connectionType) {
            return false;
        }
        if (Double.doubleToLongBits(currentDataStreamingLoading) != Double
            .doubleToLongBits(other.currentDataStreamingLoading)) {
            return false;
        }
        if (gwTotalNodes != other.gwTotalNodes) {
            return false;
        }
        if (gwTotalNodesNoInfo != other.gwTotalNodesNoInfo) {
            return false;
        }
        if (gwTotalNodesWithInfo != other.gwTotalNodesWithInfo) {
            return false;
        }
        if (gwTotalNodesWithSN != other.gwTotalNodesWithSN) {
            return false;
        }
        if (gwTotalNotReadyNodes != other.gwTotalNotReadyNodes) {
            return false;
        }
        if (gwTotalReadyNodes != other.gwTotalReadyNodes) {
            return false;
        }
        if (hardwareVersion == null) {
            if (other.hardwareVersion != null) {
                return false;
            }
        } else if (!hardwareVersion.equals(other.hardwareVersion)) {
            return false;
        }
        if (ipAddress == null) {
            if (other.ipAddress != null) {
                return false;
            }
        } else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (lastCommStatus != other.lastCommStatus) {
            return false;
        }
        if (lastCommStatusTimestamp != other.lastCommStatusTimestamp) {
            return false;
        }
        if (Double.doubleToLongBits(maxDataStreamingLoading) != Double.doubleToLongBits(other.maxDataStreamingLoading)) {
            return false;
        }
        if (mode != other.mode) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (port == null) {
            if (other.port != null) {
                return false;
            }
        } else if (!port.equals(other.port)) {
            return false;
        }
        if (radioVersion == null) {
            if (other.radioVersion != null) {
                return false;
            }
        } else if (!radioVersion.equals(other.radioVersion)) {
            return false;
        }
        if (radios == null) {
            if (other.radios != null) {
                return false;
            }
        } else if (!radios.equals(other.radios)) {
            return false;
        }
        if (releaseVersion == null) {
            if (other.releaseVersion != null) {
                return false;
            }
        } else if (!releaseVersion.equals(other.releaseVersion)) {
            return false;
        }
        if (routeColor != other.routeColor) {
            return false;
        }
        if (sequences == null) {
            if (other.sequences != null) {
                return false;
            }
        } else if (!sequences.equals(other.sequences)) {
            return false;
        }
        if (softwareVersion == null) {
            if (other.softwareVersion != null) {
                return false;
            }
        } else if (!softwareVersion.equals(other.softwareVersion)) {
            return false;
        }
        if (superAdmin == null) {
            if (other.superAdmin != null) {
                return false;
            }
        } else if (!superAdmin.equals(other.superAdmin)) {
            return false;
        }
        if (updateServerLogin == null) {
            if (other.updateServerLogin != null) {
                return false;
            }
        } else if (!updateServerLogin.equals(other.updateServerLogin)) {
            return false;
        }
        if (updateServerUrl == null) {
            if (other.updateServerUrl != null) {
                return false;
            }
        } else if (!updateServerUrl.equals(other.updateServerUrl)) {
            return false;
        }
        if (upperStackVersion == null) {
            if (other.upperStackVersion != null) {
                return false;
            }
        } else if (!upperStackVersion.equals(other.upperStackVersion)) {
            return false;
        }
        if (versionConflicts == null) {
            if (other.versionConflicts != null) {
                return false;
            }
        } else if (!versionConflicts.equals(other.versionConflicts)) {
            return false;
        }
        if (ipv6Prefix == null) {
            if (other.ipv6Prefix != null) {
                return false;
            }
        } else if (!ipv6Prefix.equals(other.ipv6Prefix)) {
            return false;
        }
        if (ipv6PrefixSuggested == null) {
            if (other.ipv6PrefixSuggested != null) {
                return false;
            }
        } else if (!ipv6PrefixSuggested.equals(other.ipv6PrefixSuggested)) {
            return false;
        }
        return true;
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
        private Authentication admin;
        private Authentication superAdmin;
        private String collectionSchedule;
        private Set<DataSequence> sequences;
        private short routeColor;
        private String updateServerUrl;
        private Authentication updateServerLogin;
        private double currentDataStreamingLoading;
        private double maxDataStreamingLoading;
        private int gwTotalNodes;
        private int gwTotalReadyNodes;
        private int gwTotalNotReadyNodes;
        private int gwTotalNodesWithSN;
        private int gwTotalNodesWithInfo;
        private int gwTotalNodesNoInfo;
        private String ipv6Prefix;
        private String ipv6PrefixSuggested;
        
        public RfnGatewayData build() {
            
            return new RfnGatewayData(name, hardwareVersion, softwareVersion, upperStackVersion, radioVersion, 
                                      releaseVersion, versionConflicts, mode, connectionType, ipAddress, port, 
                                      connectionStatus, lastCommStatus, lastCommStatusTimestamp, radios, admin,
                                      superAdmin, collectionSchedule, sequences, routeColor,updateServerUrl,
                                      updateServerLogin, currentDataStreamingLoading, maxDataStreamingLoading,
                                      gwTotalNodes, gwTotalReadyNodes, gwTotalNotReadyNodes, gwTotalNodesWithSN,
                                      gwTotalNodesWithInfo, gwTotalNodesNoInfo, ipv6Prefix, ipv6PrefixSuggested);
            
        }
        
        public Builder copyOf(RfnGatewayData oldData) {
            
            name = oldData.getName();
            hardwareVersion = oldData.getHardwareVersion();
            softwareVersion = oldData.getSoftwareVersion();
            upperStackVersion = oldData.getUpperStackVersion();
            radioVersion = oldData.getRadioVersion();
            releaseVersion = oldData.getReleaseVersion();
            versionConflicts = oldData.getVersionConflicts();
            mode = oldData.getMode();
            connectionType = oldData.getConnectionType();
            ipAddress = oldData.getIpAddress();
            port = oldData.getPort();
            connectionStatus = oldData.getConnectionStatus();
            lastCommStatus = oldData.getLastCommStatus();
            lastCommStatusTimestamp = oldData.getLastCommStatusTimestamp();
            radios = oldData.getRadios();
            admin = oldData.getAdmin();
            superAdmin = oldData.getSuperAdmin();
            collectionSchedule = oldData.getCollectionSchedule();
            sequences = oldData.getSequences();
            routeColor = oldData.getRouteColor();
            updateServerUrl = oldData.getUpdateServerUrl();
            updateServerLogin = oldData.getUpdateServerLogin();
            currentDataStreamingLoading = oldData.currentDataStreamingLoading;
            maxDataStreamingLoading = oldData.maxDataStreamingLoading;
            gwTotalNodes = oldData.gwTotalNodes;
            gwTotalReadyNodes = oldData.gwTotalNodes;
            gwTotalNotReadyNodes = oldData.gwTotalNodes;
            gwTotalNodesWithSN = oldData.gwTotalNodes;
            gwTotalNodesWithInfo = oldData.gwTotalNodes;
            gwTotalNodesNoInfo = oldData.gwTotalNodes;
            ipv6Prefix = oldData.getIpv6Prefix();
            ipv6PrefixSuggested = oldData.getIpv6PrefixSuggested();
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
        
        public Builder updateServerUrl(String updateServerUrl) {
            this.updateServerUrl = updateServerUrl;
            return this;
        }
        
        public Builder updateServerLogin(Authentication updateServerLogin) {
            this.updateServerLogin = updateServerLogin;
            return this;
        }
        
        public Builder currentDataStreamingLoading(double currentDataStreamingLoading) {
            this.currentDataStreamingLoading = currentDataStreamingLoading;
            return this;
        }
        
        public Builder maxDataStreamingLoading(double maxDataStreamingLoading) {
            this.maxDataStreamingLoading = maxDataStreamingLoading;
            return this;
        }
        
        public Builder gwTotalNodes(int gwTotalNodes) {
            this.gwTotalNodes = gwTotalNodes;
            return this;
        }
        
        public Builder gwTotalReadyNodes(int gwTotalReadyNodes) {
            this.gwTotalReadyNodes = gwTotalReadyNodes;
            return this;
        }

        public Builder gwTotalNotReadyNodes(int gwTotalNotReadyNodes) {
            this.gwTotalNotReadyNodes = gwTotalNotReadyNodes;
            return this;
        }
     
        public Builder gwTotalNodesWithSN(int gwTotalNodesWithSN) {
            this.gwTotalNodesWithSN = gwTotalNodesWithSN;
            return this;
        }
        
        public Builder gwTotalNodesWithInfo(int gwTotalNodesWithInfo) {
            this.gwTotalNodesWithInfo = gwTotalNodesWithInfo;
            return this;
        }

        public Builder gwTotalNodesNoInfo(int gwTotalNodesNoInfo) {
            this.gwTotalNodesNoInfo = gwTotalNodesNoInfo;
            return this;
        }
    }
}