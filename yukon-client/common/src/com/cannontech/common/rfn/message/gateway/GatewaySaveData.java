package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;


/**
 * Represents the data needed to perform a save operation (create or edit).
 * Shared by the {@link GatewayCreateRequest} and {@link GatewayEditRequest} classes.
 */
public class GatewaySaveData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public enum AccessLevel { USER, ADMIN, SUPER_ADMIN }
    
    // Name to be used for Yukon to tell NM to update the NM database
    private String name;
    // IP address that NM will use to connect to the gateway.
    private String ipAddress;
    private Authentication superAdmin;
    private Authentication admin;
    
    // Default gateway access level is ADMIN
    private AccessLevel defaultAccessLevel = AccessLevel.ADMIN;
    private String updateServerUrl;
    private Authentication updateServerLogin;
    
    // Port that NM will use to connect to the gateway.
    private Integer port;

    // IP address that the gateway should use to connect to NM.
    private String nmIpAddress;
    // Port that the gateway should use to connect to NM.
    private Integer nmPort;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
    
    public AccessLevel getDefaultAccessLevel() {
        return defaultAccessLevel;
    }
    
    public void setDefaultAccessLevel(AccessLevel defaultAccessLevel) {
        this.defaultAccessLevel = defaultAccessLevel;
    } 
    
    public String getUpdateServerUrl() {
        return updateServerUrl;
    }

    public void setUpdateServerUrl(String updateServerUrl) {
        this.updateServerUrl = updateServerUrl;
    }
    
    public Authentication getUpdateServerLogin() {
        return updateServerLogin;
    }

    public void setUpdateServerLogin(Authentication updateServerLogin) {
        this.updateServerLogin = updateServerLogin;
    }

    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }

    public String getNmIpAddress() {
        return nmIpAddress;
    }

    public void setNmIpAddress(String nmIpAddress) {
        this.nmIpAddress = nmIpAddress;
    }

    public Integer getNmPort() {
        return nmPort;
    }

    public void setNmPort(Integer nmPort) {
        this.nmPort = nmPort;
    }
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((admin == null) ? 0 : admin.hashCode());
        result = prime * result + ((defaultAccessLevel == null) ? 0 : defaultAccessLevel.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nmIpAddress == null) ? 0 : nmIpAddress.hashCode());
        result = prime * result + ((nmPort == null) ? 0 : nmPort.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((superAdmin == null) ? 0 : superAdmin.hashCode());
        result = prime * result + ((updateServerLogin == null) ? 0 : updateServerLogin.hashCode());
        result = prime * result + ((updateServerUrl == null) ? 0 : updateServerUrl.hashCode());
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
        GatewaySaveData other = (GatewaySaveData) obj;
        if (admin == null) {
            if (other.admin != null)
                return false;
        } else if (!admin.equals(other.admin))
            return false;
        if (defaultAccessLevel != other.defaultAccessLevel)
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (nmIpAddress == null) {
            if (other.nmIpAddress != null)
                return false;
        } else if (!nmIpAddress.equals(other.nmIpAddress))
            return false;
        if (nmPort == null) {
            if (other.nmPort != null)
                return false;
        } else if (!nmPort.equals(other.nmPort))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (superAdmin == null) {
            if (other.superAdmin != null)
                return false;
        } else if (!superAdmin.equals(other.superAdmin))
            return false;
        if (updateServerLogin == null) {
            if (other.updateServerLogin != null)
                return false;
        } else if (!updateServerLogin.equals(other.updateServerLogin))
            return false;
        if (updateServerUrl == null) {
            if (other.updateServerUrl != null)
                return false;
        } else if (!updateServerUrl.equals(other.updateServerUrl))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "GatewaySaveData [name=%s, ipAddress=%s, superAdmin=%s, admin=%s, defaultAccessLevel=%s, updateServerUrl=%s, updateServerLogin=%s, port=%s, nmIpAddress=%s, nmPort=%s]",
                name,
                ipAddress,
                superAdmin,
                admin,
                defaultAccessLevel,
                updateServerUrl,
                updateServerLogin,
                port,
                nmIpAddress,
                nmPort);
    }
    
}