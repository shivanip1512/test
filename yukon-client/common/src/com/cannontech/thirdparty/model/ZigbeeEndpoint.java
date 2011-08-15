package com.cannontech.thirdparty.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

public class ZigbeeEndpoint implements YukonDevice {

    private PaoIdentifier paoIdentifier;
    private String installCode;
    private String macAddress;
    private int nodeId;
    private int destinationEndPointId;
    private String name;
    private Integer gatewayId;
    
    public String getInstallCode() {
        return installCode;
    }

    public void setInstallCode(String installCode) {
        this.installCode = installCode;
    }
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }   
    
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Integer getGatewayId() {
        return gatewayId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getDestinationEndPointId() {
        return destinationEndPointId;
    }

    public void setDestinationEndPointId(int destinationEndPointId) {
        this.destinationEndPointId = destinationEndPointId;
    }
    
}
