package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.Locatable;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnGateway extends RfnDevice implements Locatable {
    private RfnGatewayData gatewayData;
    private PaoLocation paoLocation;
    private String name;
    
    public RfnGateway(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData gatewayData) {
        super(pao, rfnIdentifier);
        this.gatewayData = gatewayData;
        this.name = name;
    }
    
    public RfnGatewayData getData() {
        return gatewayData;
    }
    
    @Override
    public PaoLocation getLocation() {
        return paoLocation;
    }
    
    public void setLocation(PaoLocation paoLocation) {
        this.paoLocation = paoLocation;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
