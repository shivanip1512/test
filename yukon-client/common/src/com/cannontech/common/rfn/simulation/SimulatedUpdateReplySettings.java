package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.gateway.GatewayConfigResult;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;

public class SimulatedUpdateReplySettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private GatewayUpdateResult createResult;
    private GatewayUpdateResult editResult;
    private GatewayUpdateResult deleteResult;
    private GatewayConfigResult ipv6PrefixUpdateResult;
    
    public void setCreateResult(GatewayUpdateResult createResult) {
        this.createResult = createResult;
    }

    public void setEditResult(GatewayUpdateResult editResult) {
        this.editResult = editResult;
    }

    public void setDeleteResult(GatewayUpdateResult deleteResult) {
        this.deleteResult = deleteResult;
    }

    public GatewayUpdateResult getCreateResult() {
        return createResult;
    }
    
    public GatewayUpdateResult getEditResult() {
        return editResult;
    }
    
    public GatewayUpdateResult getDeleteResult() {
        return deleteResult;
    }

    public GatewayConfigResult getIpv6PrefixUpdateResult() {
        return ipv6PrefixUpdateResult;
    }

    public void setIpv6PrefixUpdateResult(GatewayConfigResult ipv6PrefixUpdateResult) {
        this.ipv6PrefixUpdateResult = ipv6PrefixUpdateResult;
    }
}
