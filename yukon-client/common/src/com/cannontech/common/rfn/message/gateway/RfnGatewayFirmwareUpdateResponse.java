package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Response to an RfnGatewayFirmwareUpdateRequest.
 * Queue: yukon.qr.obj.common.rfn.RfnGatewayFirmwareUpdateResponse
 */
public class RfnGatewayFirmwareUpdateResponse implements Serializable, RfnIdentifyingMessage {
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier gateway;
    private Integer updateId;
    private GatewayFirmwareUpdateRequestResult result;
    
    public RfnIdentifier getRfnIdentifier() {
        return gateway;
    }
    
    public RfnIdentifier getGateway() {
        return gateway;
    }
    
    public void setGateway(RfnIdentifier gateway) {
        this.gateway = gateway;
    }
    
    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    public GatewayFirmwareUpdateRequestResult getResult() {
        return result;
    }
    
    public void setResult(GatewayFirmwareUpdateRequestResult result) {
        this.result = result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((updateId == null) ? 0 : updateId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RfnGatewayFirmwareUpdateResponse other = (RfnGatewayFirmwareUpdateResponse) obj;
        if (gateway == null) {
            if (other.gateway != null) {
                return false;
            }
        } else if (!gateway.equals(other.gateway)) {
            return false;
        }
        if (result != other.result) {
            return false;
        }
        if (updateId == null) {
            if (other.updateId != null) {
                return false;
            }
        } else if (!updateId.equals(other.updateId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RfnGatewayFirmwareUpdateResponse [gateway=" + gateway + ", updateId=" + updateId + ", result=" + result
               + "]";
    }
    
}
