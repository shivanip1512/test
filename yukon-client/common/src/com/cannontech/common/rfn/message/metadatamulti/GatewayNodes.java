package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class GatewayNodes implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Set<RfnIdentifier> readyNodes = new HashSet<>();
    
    private Set<RfnIdentifier> notReadyNodes = new HashSet<>();
    
    private Set<RfnIdentifier> unknownNodes = new HashSet<>();

    public Set<RfnIdentifier> getReadyNodes() {
        return readyNodes;
    }

    public void setReadyNodes(Set<RfnIdentifier> readyNodes) {
        this.readyNodes = readyNodes;
    }

    public Set<RfnIdentifier> getNotReadyNodes() {
        return notReadyNodes;
    }

    public void setNotReadyNodes(Set<RfnIdentifier> notReadyNodes) {
        this.notReadyNodes = notReadyNodes;
    }

    public Set<RfnIdentifier> getUnknownNodes() {
        return unknownNodes;
    }

    public void setUnknownNodes(Set<RfnIdentifier> unknownNodes) {
        this.unknownNodes = unknownNodes;
    }
    
    public Set<RfnIdentifier> getAllNodes(){
        Set<RfnIdentifier> allNodes = new HashSet<>();
        allNodes.addAll(readyNodes);
        allNodes.addAll(notReadyNodes);
        allNodes.addAll(unknownNodes);
        return allNodes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((notReadyNodes == null) ? 0 : notReadyNodes.hashCode());
        result = prime * result + ((readyNodes == null) ? 0 : readyNodes.hashCode());
        result = prime * result + ((unknownNodes == null) ? 0 : unknownNodes.hashCode());
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
        GatewayNodes other = (GatewayNodes) obj;
        if (notReadyNodes == null) {
            if (other.notReadyNodes != null)
                return false;
        } else if (!notReadyNodes.equals(other.notReadyNodes))
            return false;
        if (readyNodes == null) {
            if (other.readyNodes != null)
                return false;
        } else if (!readyNodes.equals(other.readyNodes))
            return false;
        if (unknownNodes == null) {
            if (other.unknownNodes != null)
                return false;
        } else if (!unknownNodes.equals(other.unknownNodes))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("GatewayNodes [readyNodes=%s, notReadyNodes=%s, unknownNodes=%s]",
                             readyNodes,
                             notReadyNodes,
                             unknownNodes);
    }
}