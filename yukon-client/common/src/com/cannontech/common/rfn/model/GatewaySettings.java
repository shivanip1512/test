package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.gateway.Authentication;

public class GatewaySettings {

    private Integer id;
    private String name;
    private String ipAddress;
    private boolean useDefaultPort;
    private Integer port;
    private Authentication admin;
    private Authentication superAdmin;
    private Double latitude;
    private Double longitude;
    private String updateServerUrl;
    private Authentication updateServerLogin;
    private boolean useDefaultUpdateServer;
    // IP address that the gateway should use to connect to NM.
    private String nmIpAddress;
    // Port that the gateway should use to connect to NM.
    private Integer nmPort;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public boolean isUseDefaultPort() {
        return useDefaultPort;
    }

    public void setUseDefaultPort(boolean useDefaultPort) {
        this.useDefaultPort = useDefaultPort;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Authentication getAdmin() {
        return admin;
    }

    public void setAdmin(Authentication admin) {
        this.admin = admin;
    }

    public Authentication getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Authentication superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public boolean isUseDefaultUpdateServer() {
        return useDefaultUpdateServer;
    }

    public void setUseDefaultUpdateServer(boolean useDefaultUpdateServer) {
        this.useDefaultUpdateServer = useDefaultUpdateServer;
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
    public String toString() {
        return String.format(
                "GatewaySettings [id=%s, name=%s, ipAddress=%s, port=%s, admin=%s, superAdmin=%s, latitude=%s, longitude=%s, updateServerUrl=%s, updateServerLogin=%s, useDefault=%s, nmIpAddress=%s, nmPort=%s]",
                id, name, ipAddress, port, admin, superAdmin, latitude, longitude, updateServerUrl, updateServerLogin,useDefaultUpdateServer, nmIpAddress, nmPort);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((admin == null) ? 0 : admin.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
        result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nmIpAddress == null) ? 0 : nmIpAddress.hashCode());
        result = prime * result + ((nmPort == null) ? 0 : nmPort.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((superAdmin == null) ? 0 : superAdmin.hashCode());
        result = prime * result + ((updateServerLogin == null) ? 0 : updateServerLogin.hashCode());
        result = prime * result + ((updateServerUrl == null) ? 0 : updateServerUrl.hashCode());
        result = prime * result + (useDefaultPort ? 1231 : 1237);
        result = prime * result + (useDefaultUpdateServer ? 1231 : 1237);
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
        GatewaySettings other = (GatewaySettings) obj;
        if (admin == null) {
            if (other.admin != null)
                return false;
        } else if (!admin.equals(other.admin))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (latitude == null) {
            if (other.latitude != null)
                return false;
        } else if (!latitude.equals(other.latitude))
            return false;
        if (longitude == null) {
            if (other.longitude != null)
                return false;
        } else if (!longitude.equals(other.longitude))
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
        if (useDefaultPort != other.useDefaultPort)
            return false;
        if (useDefaultUpdateServer != other.useDefaultUpdateServer)
            return false;
        return true;
    } 
}