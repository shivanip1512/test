package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

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
    public String toString() {
        return "CompleteZbEndpoint [installCode=" + installCode + ", macAddress=" + macAddress
               + ", endPointId=" + endPointId + ", nodeId=" + nodeId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + endPointId;
        result = prime * result + ((installCode == null) ? 0 : installCode.hashCode());
        result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
        result = prime * result + nodeId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteZbEndpoint other = (CompleteZbEndpoint) obj;
        if (endPointId != other.endPointId)
            return false;
        if (installCode == null) {
            if (other.installCode != null)
                return false;
        } else if (!installCode.equals(other.installCode))
            return false;
        if (macAddress == null) {
            if (other.macAddress != null)
                return false;
        } else if (!macAddress.equals(other.macAddress))
            return false;
        if (nodeId != other.nodeId)
            return false;
        return true;
    }
}
