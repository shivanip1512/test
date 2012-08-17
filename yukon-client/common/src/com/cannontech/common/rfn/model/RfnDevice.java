package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnDevice implements YukonRfn, YukonDevice, DisplayablePao {
    
    private RfnIdentifier rfnIdentifier;
    private PaoIdentifier paoIdentifier;

    public RfnDevice() {}
    
    public RfnDevice(PaoIdentifier paoIdentifier, RfnIdentifier rfnIdentifier) {
        this.paoIdentifier = paoIdentifier;
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public RfnDevice(YukonPao pao, RfnIdentifier rfnIdentifier) {
        this.paoIdentifier = pao.getPaoIdentifier();
        this.rfnIdentifier = rfnIdentifier;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    @Override
    public String getName() {
        // arbitrary guess!
        return rfnIdentifier.getSensorSerialNumber();
    }

    @Override
    public String toString() {
        return String.format("RfnDevice [rfnIdentifier=%s, paoIdentifier=%s]", rfnIdentifier, paoIdentifier);
    }
    
}