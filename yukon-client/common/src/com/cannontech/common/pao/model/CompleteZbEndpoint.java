package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName="DeviceId", paoTypes=PaoType.ZIGBEE_ENDPOINT)
public class CompleteZbEndpoint extends CompleteDevice {
    private String installCode;
    private String macAddress;
    private int endPointId = 1;  /*Constant place holder until Firmware change*/
    private int nodeId = 0; /*Constant place holder until Firmware change*/

    @YukonPaoField
    public String getInstallCode() {
        return installCode;
    }

    public void setInstallCode(String installCode) {
        this.installCode = installCode;
    }

    @YukonPaoField
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @YukonPaoField(columnName="DestinationEndPointId")
    public int getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(int endPointId) {
        this.endPointId = endPointId;
    }

    @YukonPaoField
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), installCode, macAddress, endPointId, nodeId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteZbEndpoint) {
            if (!super.equals(object)) 
                return false;
            CompleteZbEndpoint that = (CompleteZbEndpoint) object;
            return Objects.equal(this.installCode, that.installCode)
                && Objects.equal(this.macAddress, that.macAddress)
                && Objects.equal(this.endPointId, that.endPointId)
                && Objects.equal(this.nodeId, that.nodeId);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteZbEndpoint [installCode=" + installCode + ", macAddress=" + macAddress
               + ", endPointId=" + endPointId + ", nodeId=" + nodeId + "]";
    }
}
