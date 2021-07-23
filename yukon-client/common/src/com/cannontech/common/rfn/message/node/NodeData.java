package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NodeData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeSerialNumber;

    private NodeType nodeType;

    private Long inNetworkTimestamp;

    private String macAddress;

    private String networkAddress; // A propriety EkaNet network address

    private String hardwareVersion;

    private String firmwareVersion; // Software Version

    private String productNumber;

    private String meterConfigID;

    private String secondaryModuleFirmwareVersion;

    private Long bootLoaderVersion;

    private WifiSuperMeterData wifiSuperMeterData;

    private CellularIplinkRelayData cellularIplinkRelayData;

    // A string represents an ipv6 address, e.x., "FD30:0000:0000:0001:0214:08FF:FE0A:BF91"
    // Yukon UI can display it directly without adding colons.
    private String nodeIpv6Address;

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

    public Long getInNetworkTimestamp() {
        return inNetworkTimestamp;
    }

    public void setInNetworkTimestamp(Long inNetworkTimestamp) {
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

    public String getSecondaryModuleFirmwareVersion() {
        return secondaryModuleFirmwareVersion;
    }

    public void setSecondaryModuleFirmwareVersion(String secondaryModuleFirmwareVersion) {
        this.secondaryModuleFirmwareVersion = secondaryModuleFirmwareVersion;
    }

    public Long getBootLoaderVersion() {
        return bootLoaderVersion;
    }

    public void setBootLoaderVersion(Long bootLoaderVersion) {
        this.bootLoaderVersion = bootLoaderVersion;
    }

    public WifiSuperMeterData getWifiSuperMeterData() {
        return wifiSuperMeterData;
    }

    public void setWifiSuperMeterData(WifiSuperMeterData wifiSuperMeterData) {
        this.wifiSuperMeterData = wifiSuperMeterData;
    }

    public CellularIplinkRelayData getCellularIplinkRelayData() {
        return cellularIplinkRelayData;
    }

    public void setCellularIplinkRelayData(CellularIplinkRelayData cellularIplinkRelayData) {
        this.cellularIplinkRelayData = cellularIplinkRelayData;
    }

    public String getNodeIpv6Address() {
        return nodeIpv6Address;
    }

    public void setNodeIpv6Address(String nodeIpv6Address) {
        this.nodeIpv6Address = nodeIpv6Address;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bootLoaderVersion == null) ? 0 : bootLoaderVersion.hashCode());
        result = prime * result
                + ((cellularIplinkRelayData == null) ? 0 : cellularIplinkRelayData.hashCode());
        result = prime * result + ((firmwareVersion == null) ? 0 : firmwareVersion.hashCode());
        result = prime * result + ((hardwareVersion == null) ? 0 : hardwareVersion.hashCode());
        result = prime * result + ((inNetworkTimestamp == null) ? 0 : inNetworkTimestamp.hashCode());
        result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
        result = prime * result + ((meterConfigID == null) ? 0 : meterConfigID.hashCode());
        result = prime * result + ((networkAddress == null) ? 0 : networkAddress.hashCode());
        result = prime * result + ((nodeIpv6Address == null) ? 0 : nodeIpv6Address.hashCode());
        result = prime * result + ((nodeSerialNumber == null) ? 0 : nodeSerialNumber.hashCode());
        result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result + ((productNumber == null) ? 0 : productNumber.hashCode());
        result = prime * result + ((secondaryModuleFirmwareVersion == null) ? 0
                : secondaryModuleFirmwareVersion.hashCode());
        result = prime * result + ((wifiSuperMeterData == null) ? 0 : wifiSuperMeterData.hashCode());
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
        if (bootLoaderVersion == null) {
            if (other.bootLoaderVersion != null)
                return false;
        } else if (!bootLoaderVersion.equals(other.bootLoaderVersion))
            return false;
        if (cellularIplinkRelayData == null) {
            if (other.cellularIplinkRelayData != null)
                return false;
        } else if (!cellularIplinkRelayData.equals(other.cellularIplinkRelayData))
            return false;
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
        if (inNetworkTimestamp == null) {
            if (other.inNetworkTimestamp != null)
                return false;
        } else if (!inNetworkTimestamp.equals(other.inNetworkTimestamp))
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
        if (nodeIpv6Address == null) {
            if (other.nodeIpv6Address != null)
                return false;
        } else if (!nodeIpv6Address.equals(other.nodeIpv6Address))
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
        if (secondaryModuleFirmwareVersion == null) {
            if (other.secondaryModuleFirmwareVersion != null)
                return false;
        } else if (!secondaryModuleFirmwareVersion.equals(other.secondaryModuleFirmwareVersion))
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}