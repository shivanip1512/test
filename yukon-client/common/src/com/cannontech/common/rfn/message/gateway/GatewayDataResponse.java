package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Message sent to Yukon on a temporary queue as a result of a {@link GatewayDataRequest}
 * or received unsolicited on the yukon.qr.obj.common.rfn.GatewayData queue as a result 
 * of data changing in NM. 
 */
public class GatewayDataResponse implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    
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
    private short routeColor;
    
    private Authentication superAdmin;
    private Authentication admin;
    
    private String collectionSchedule; // Cron string
    private Set<DataSequence> sequences;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public String getHardwareVersion() {
        return hardwareVersion;
    }
    
    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }
    
    public String getSoftwareVersion() {
        return softwareVersion;
    }
    
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }
    
    public String getUpperStackVersion() {
        return upperStackVersion;
    }
    
    public void setUpperStackVersion(String upperStackVersion) {
        this.upperStackVersion = upperStackVersion;
    }
    
    public String getRadioVersion() {
        return radioVersion;
    }
    
    public void setRadioVersion(String radioVersion) {
        this.radioVersion = radioVersion;
    }
    
    public String getReleaseVersion() {
        return releaseVersion;
    }
    
    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }
    
    public Set<ConflictType> getVersionConflicts() {
        return versionConflicts;
    }
    
    public void setVersionConflicts(Set<ConflictType> versionConflicts) {
        this.versionConflicts = versionConflicts;
    }
    
    public AppMode getMode() {
        return mode;
    }
    
    public void setMode(AppMode mode) {
        this.mode = mode;
    }
    
    public ConnectionType getConnectionType() {
        return connectionType;
    }
    
    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
    
    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public LastCommStatus getLastCommStatus() {
        return lastCommStatus;
    }
    
    public void setLastCommStatus(LastCommStatus lastCommStatus) {
        this.lastCommStatus = lastCommStatus;
    }
    
    public long getLastCommStatusTimestamp() {
        return lastCommStatusTimestamp;
    }
    
    public void setLastCommStatusTimestamp(long lastCommStatusTimestamp) {
        this.lastCommStatusTimestamp = lastCommStatusTimestamp;
    }
    
    public Set<Radio> getRadios() {
        return radios;
    }
    
    public void setRadios(Set<Radio> radios) {
        this.radios = radios;
    }
    
    public Authentication getSuperAdmin() {
        return superAdmin;
    }
    
    public void setSuperAdmin(Authentication superAdmin) {
        this.superAdmin = superAdmin;
    }
    
    public Authentication getAdmin() {
        return admin;
    }
    
    public void setAdmin(Authentication admin) {
        this.admin = admin;
    }
    
    public String getCollectionSchedule() {
        return collectionSchedule;
    }
    
    public void setCollectionSchedule(String collectionSchedule) {
        this.collectionSchedule = collectionSchedule;
    }
    
    public Set<DataSequence> getSequences() {
        return sequences;
    }
    
    public void setSequences(Set<DataSequence> sequences) {
        this.sequences = sequences;
    }
    
    public short getRouteColor() {
        return routeColor;
    }
    
    public void setRouteColor(short routeColor) {
        this.routeColor = routeColor;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((admin == null) ? 0 : admin.hashCode());
        result =
            prime * result + ((collectionSchedule == null) ? 0 : collectionSchedule.hashCode());
        result = prime * result + ((connectionStatus == null) ? 0 : connectionStatus.hashCode());
        result = prime * result + ((connectionType == null) ? 0 : connectionType.hashCode());
        result = prime * result + ((hardwareVersion == null) ? 0 : hardwareVersion.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((lastCommStatus == null) ? 0 : lastCommStatus.hashCode());
        result =
            prime * result + (int) (lastCommStatusTimestamp ^ (lastCommStatusTimestamp >>> 32));
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((radioVersion == null) ? 0 : radioVersion.hashCode());
        result = prime * result + ((radios == null) ? 0 : radios.hashCode());
        result = prime * result + ((releaseVersion == null) ? 0 : releaseVersion.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + routeColor;
        result = prime * result + ((sequences == null) ? 0 : sequences.hashCode());
        result = prime * result + ((softwareVersion == null) ? 0 : softwareVersion.hashCode());
        result = prime * result + ((superAdmin == null) ? 0 : superAdmin.hashCode());
        result = prime * result + ((upperStackVersion == null) ? 0 : upperStackVersion.hashCode());
        result = prime * result + ((versionConflicts == null) ? 0 : versionConflicts.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GatewayDataResponse other = (GatewayDataResponse) obj;
        if (admin == null) {
            if (other.admin != null)
                return false;
        } else if (!admin.equals(other.admin))
            return false;
        if (collectionSchedule == null) {
            if (other.collectionSchedule != null)
                return false;
        } else if (!collectionSchedule.equals(other.collectionSchedule))
            return false;
        if (connectionStatus != other.connectionStatus)
            return false;
        if (connectionType != other.connectionType)
            return false;
        if (hardwareVersion == null) {
            if (other.hardwareVersion != null)
                return false;
        } else if (!hardwareVersion.equals(other.hardwareVersion))
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (lastCommStatus != other.lastCommStatus)
            return false;
        if (lastCommStatusTimestamp != other.lastCommStatusTimestamp)
            return false;
        if (mode != other.mode)
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (radioVersion == null) {
            if (other.radioVersion != null)
                return false;
        } else if (!radioVersion.equals(other.radioVersion))
            return false;
        if (radios == null) {
            if (other.radios != null)
                return false;
        } else if (!radios.equals(other.radios))
            return false;
        if (releaseVersion == null) {
            if (other.releaseVersion != null)
                return false;
        } else if (!releaseVersion.equals(other.releaseVersion))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (routeColor != other.routeColor)
            return false;
        if (sequences == null) {
            if (other.sequences != null)
                return false;
        } else if (!sequences.equals(other.sequences))
            return false;
        if (softwareVersion == null) {
            if (other.softwareVersion != null)
                return false;
        } else if (!softwareVersion.equals(other.softwareVersion))
            return false;
        if (superAdmin == null) {
            if (other.superAdmin != null)
                return false;
        } else if (!superAdmin.equals(other.superAdmin))
            return false;
        if (upperStackVersion == null) {
            if (other.upperStackVersion != null)
                return false;
        } else if (!upperStackVersion.equals(other.upperStackVersion))
            return false;
        if (versionConflicts == null) {
            if (other.versionConflicts != null)
                return false;
        } else if (!versionConflicts.equals(other.versionConflicts))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("GatewayDataResponse [rfnIdentifier=%s, hardwareVersion=%s, softwareVersion=%s, upperStackVersion=%s, radioVersion=%s, releaseVersion=%s, versionConflicts=%s, mode=%s, connectionType=%s, ipAddress=%s, port=%s, connectionStatus=%s, lastCommStatus=%s, lastCommStatusTimestamp=%s, radios=%s, routeColor=%s, superAdmin=%s, admin=%s, collectionSchedule=%s, sequences=%s]",
                    rfnIdentifier,
                    hardwareVersion,
                    softwareVersion,
                    upperStackVersion,
                    radioVersion,
                    releaseVersion,
                    versionConflicts,
                    mode,
                    connectionType,
                    ipAddress,
                    port,
                    connectionStatus,
                    lastCommStatus,
                    lastCommStatusTimestamp,
                    radios,
                    routeColor,
                    superAdmin,
                    admin,
                    collectionSchedule,
                    sequences);
    }
}