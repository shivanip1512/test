package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class RelayCellularComm implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier deviceRfnIdentifier; // can't be null
    
    private RfnIdentifier gatewayRfnIdentifier; // null indicates no primary gateway
    
    private NodeConnectionState nodeConnectionState; // null indicates unknown Communication Status
    
    private long cellularCommStatusTimestamp; // Node Communication Status obtained at
    
    private Integer rssi; // nullable
    
    private Integer sinr; // nullable
    
    private Integer rsrp; // nullable
    
    private Integer rsrq; // nullable

    public RfnIdentifier getDeviceRfnIdentifier() {
        return deviceRfnIdentifier;
    }

    public void setDeviceRfnIdentifier(RfnIdentifier deviceRfnIdentifier) {
        this.deviceRfnIdentifier = deviceRfnIdentifier;
    }

    public RfnIdentifier getGatewayRfnIdentifier() {
        return gatewayRfnIdentifier;
    }

    public void setGatewayRfnIdentifier(RfnIdentifier gatewayRfnIdentifier) {
        this.gatewayRfnIdentifier = gatewayRfnIdentifier;
    }

    public NodeConnectionState getNodeConnectionState() {
        return nodeConnectionState;
    }

    public void setNodeConnectionState(NodeConnectionState nodeConnectionState) {
        this.nodeConnectionState = nodeConnectionState;
    }

    public long getCellularCommStatusTimestamp() {
        return cellularCommStatusTimestamp;
    }

    public void setCellularCommStatusTimestamp(long cellularCommStatusTimestamp) {
        this.cellularCommStatusTimestamp = cellularCommStatusTimestamp;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getSinr() {
        return sinr;
    }

    public void setSinr(Integer sinr) {
        this.sinr = sinr;
    }

    public Integer getRsrp() {
        return rsrp;
    }

    public void setRsrp(Integer rsrp) {
        this.rsrp = rsrp;
    }

    public Integer getRsrq() {
        return rsrq;
    }

    public void setRsrq(Integer rsrq) {
        this.rsrq = rsrq;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + (int) (cellularCommStatusTimestamp ^ (cellularCommStatusTimestamp >>> 32));
        result =
            prime * result + ((deviceRfnIdentifier == null) ? 0 : deviceRfnIdentifier.hashCode());
        result =
            prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        result =
            prime * result + ((nodeConnectionState == null) ? 0 : nodeConnectionState.hashCode());
        result = prime * result + ((rsrp == null) ? 0 : rsrp.hashCode());
        result = prime * result + ((rsrq == null) ? 0 : rsrq.hashCode());
        result = prime * result + ((rssi == null) ? 0 : rssi.hashCode());
        result = prime * result + ((sinr == null) ? 0 : sinr.hashCode());
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
        RelayCellularComm other = (RelayCellularComm) obj;
        if (cellularCommStatusTimestamp != other.cellularCommStatusTimestamp)
            return false;
        if (deviceRfnIdentifier == null) {
            if (other.deviceRfnIdentifier != null)
                return false;
        } else if (!deviceRfnIdentifier.equals(other.deviceRfnIdentifier))
            return false;
        if (gatewayRfnIdentifier == null) {
            if (other.gatewayRfnIdentifier != null)
                return false;
        } else if (!gatewayRfnIdentifier.equals(other.gatewayRfnIdentifier))
            return false;
        if (nodeConnectionState != other.nodeConnectionState)
            return false;
        if (rsrp == null) {
            if (other.rsrp != null)
                return false;
        } else if (!rsrp.equals(other.rsrp))
            return false;
        if (rsrq == null) {
            if (other.rsrq != null)
                return false;
        } else if (!rsrq.equals(other.rsrq))
            return false;
        if (rssi == null) {
            if (other.rssi != null)
                return false;
        } else if (!rssi.equals(other.rssi))
            return false;
        if (sinr == null) {
            if (other.sinr != null)
                return false;
        } else if (!sinr.equals(other.sinr))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RelayCellularComm [deviceRfnIdentifier=" + deviceRfnIdentifier
            + ", gatewayRfnIdentifier=" + gatewayRfnIdentifier + ", nodeConnectionState="
            + nodeConnectionState + ", cellularCommStatusTimestamp=" + cellularCommStatusTimestamp
            + ", rssi=" + rssi + ", sinr=" + sinr + ", rsrp=" + rsrp + ", rsrq=" + rsrq + "]";
    }
}