package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

public class NodeData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nodeSerialNumber;
    
    private NodeType nodeType;
    
    private long inNetworkTimestamp;
    
    private String macAddress;
    
    private String networkAddress; // A propriety EkaNet network address

    private String hardwareVersion;
    
    private String firmwareVersion; // Software Version
    
    private String productNumber;
    
    private String meterConfigID;
    
    private WifiSuperMeterData wifiSuperMeterData;

    public String getNodeSerialNumber() {
        return nodeSerialNumber;
    }

    public void setNodeSerialNumber(String nodeSerialNumber) {
        this.nodeSerialNumber = nodeSerialNumber;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public long getInNetworkTimestamp() {
        return inNetworkTimestamp;
    }

    public void setInNetworkTimestamp(long inNetworkTimestamp) {
        this.inNetworkTimestamp = inNetworkTimestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getMeterConfigID() {
        return meterConfigID;
    }

    public void setMeterConfigID(String meterConfigID) {
        this.meterConfigID = meterConfigID;
    }
    
    public WifiSuperMeterData getWifiSuperMeterData() {
        return wifiSuperMeterData;
    }

    public void setWifiSuperMeterData(WifiSuperMeterData wifiSuperMeterData) {
        this.wifiSuperMeterData = wifiSuperMeterData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firmwareVersion == null) ? 0 : firmwareVersion.hashCode());
        result = prime * result + ((hardwareVersion == null) ? 0 : hardwareVersion.hashCode());
        result = prime * result + (int) (inNetworkTimestamp ^ (inNetworkTimestamp >>> 32));
        result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
        result = prime * result + ((meterConfigID == null) ? 0 : meterConfigID.hashCode());
        result = prime * result + ((networkAddress == null) ? 0 : networkAddress.hashCode());
        result = prime * result + ((nodeSerialNumber == null) ? 0 : nodeSerialNumber.hashCode());
        result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result + ((productNumber == null) ? 0 : productNumber.hashCode());
        result =
            prime * result + ((wifiSuperMeterData == null) ? 0 : wifiSuperMeterData.hashCode());
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
        NodeData other = (NodeData) obj;
        if (firmwareVersion == null) {
            if (other.firmwareVersion != null)
                return false;
        } else if (!firmwareVersion.equals(other.firmwareVersion))
            return false;
        if (hardwareVersion == null) {
            if (other.hardwareVersion != null)
                return false;
        } else if (!hardwareVersion.equals(other.hardwareVersion))
            return false;
        if (inNetworkTimestamp != other.inNetworkTimestamp)
            return false;
        if (macAddress == null) {
            if (other.macAddress != null)
                return false;
        } else if (!macAddress.equals(other.macAddress))
            return false;
        if (meterConfigID == null) {
            if (other.meterConfigID != null)
                return false;
        } else if (!meterConfigID.equals(other.meterConfigID))
            return false;
        if (networkAddress == null) {
            if (other.networkAddress != null)
                return false;
        } else if (!networkAddress.equals(other.networkAddress))
            return false;
        if (nodeSerialNumber == null) {
            if (other.nodeSerialNumber != null)
                return false;
        } else if (!nodeSerialNumber.equals(other.nodeSerialNumber))
            return false;
        if (nodeType != other.nodeType)
            return false;
        if (productNumber == null) {
            if (other.productNumber != null)
                return false;
        } else if (!productNumber.equals(other.productNumber))
            return false;
        if (wifiSuperMeterData == null) {
            if (other.wifiSuperMeterData != null)
                return false;
        } else if (!wifiSuperMeterData.equals(other.wifiSuperMeterData))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "NodeData [nodeSerialNumber=" + nodeSerialNumber + ", nodeType=" + nodeType
            + ", inNetworkTimestamp=" + inNetworkTimestamp + ", macAddress=" + macAddress
            + ", networkAddress=" + networkAddress + ", hardwareVersion=" + hardwareVersion
            + ", firmwareVersion=" + firmwareVersion + ", productNumber=" + productNumber
            + ", meterConfigID=" + meterConfigID + ", wifiSuperMeterData=" + wifiSuperMeterData
            + "]";
    }

}
