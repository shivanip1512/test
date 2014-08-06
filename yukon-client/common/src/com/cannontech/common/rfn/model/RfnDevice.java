package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnDevice implements YukonRfn, YukonDevice, DisplayablePao {
    private final RfnIdentifier rfnIdentifier;
    private final PaoIdentifier paoIdentifier;

    public RfnDevice(YukonPao pao, RfnIdentifier rfnIdentifier) {
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
        // arbitrary guess!
        return rfnIdentifier.getSensorSerialNumber();
    }

    @Override
    public String toString() {
        return String.format("RfnDevice [rfnIdentifier=%s, paoIdentifier=%s]", rfnIdentifier, paoIdentifier);
    }
}
