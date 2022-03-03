package com.cannontech.common.rfn.message.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeNetworkInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // NM DB allows duplicate node group names but different node group id
    // So a node may belong to multiple groups with the same group name.
    private final List<String> nodeGroupNames = new ArrayList<>();
    
    // NM DB restricts unique node name per node
    private final Set<String> nodeNames = new HashSet<>();
    
    private String hostname;
    
    private String sensorFirmwareVersion;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSensorFirmwareVersion() {
        return sensorFirmwareVersion;
    }

    public void setSensorFirmwareVersion(String sensorFirmwareVersion) {
        this.sensorFirmwareVersion = sensorFirmwareVersion;
    }

    public List<String> getNodeGroupNames() {
        return nodeGroupNames;
    }

    public Set<String> getNodeNames() {
        return nodeNames;
    }
    
    public void addNodeGroupName(String nodeGroupName) {
        nodeGroupNames.add(nodeGroupName);
    }
    
    public void addNodeName(String nodeName) {
        nodeNames.add(nodeName);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((nodeGroupNames == null) ? 0 : nodeGroupNames.hashCode());
        result = prime * result + ((nodeNames == null) ? 0 : nodeNames.hashCode());
        result = prime * result
            + ((sensorFirmwareVersion == null) ? 0 : sensorFirmwareVersion.hashCode());
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
        NodeNetworkInfo other = (NodeNetworkInfo) obj;
        if (hostname == null) {
            if (other.hostname != null)
                return false;
        } else if (!hostname.equals(other.hostname))
            return false;
        if (nodeGroupNames == null) {
            if (other.nodeGroupNames != null)
                return false;
        } else if (!nodeGroupNames.equals(other.nodeGroupNames))
            return false;
        if (nodeNames == null) {
            if (other.nodeNames != null)
                return false;
        } else if (!nodeNames.equals(other.nodeNames))
            return false;
        if (sensorFirmwareVersion == null) {
            if (other.sensorFirmwareVersion != null)
                return false;
        } else if (!sensorFirmwareVersion.equals(other.sensorFirmwareVersion))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("NodeNetworkInfo [nodeGroupNames=%s, nodeNames=%s, hostname=%s, sensorFirmwareVersion=%s]"
                                             , nodeGroupNames,    nodeNames,    hostname,    sensorFirmwareVersion);
    }
}