package com.cannontech.thirdparty.digi.dao.provider.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class UtilityProZigbeeFields implements PaoTemplatePart {
    private String installCode;
    private String macAddress;
    private int endPointId;
    private Integer nodeId;
    
    public UtilityProZigbeeFields(String installCode, String macAddress, int endPointId) {
        this.installCode = installCode;
        this.macAddress = macAddress;
        this.endPointId = endPointId;
    }
    
    public String getInstallCode() {
        return installCode;
    }

    public void setInstallCode(String installCode) {
        this.installCode = installCode;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(int endPointId) {
        this.endPointId = endPointId;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }
    
}
