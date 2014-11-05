package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnDevice implements YukonRfn, YukonDevice, DisplayablePao {
    
    protected String name;
    private final RfnIdentifier rfnIdentifier;
    private final PaoIdentifier paoIdentifier;
    
    public RfnDevice(String name, YukonPao pao, RfnIdentifier rfnIdentifier) {
        this.name = name;
        this.paoIdentifier = pao.getPaoIdentifier();
        this.rfnIdentifier = rfnIdentifier;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("RfnDevice [name=%s, rfnIdentifier=%s, paoIdentifier=%s]", name, rfnIdentifier, paoIdentifier);
    }
    
}