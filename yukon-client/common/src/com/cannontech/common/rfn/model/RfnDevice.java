package com.cannontech.common.rfn.model;

import com.cannontech.amr.rfn.model.RfnMeter;
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
        paoIdentifier = pao.getPaoIdentifier();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        RfnDevice other = (RfnDevice) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null) {
                return false;
            }
        } else if (!paoIdentifier.equals(other.paoIdentifier)) {
            return false;
        }
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null) {
                return false;
            }
        } else if (!rfnIdentifier.equals(other.rfnIdentifier)) {
            return false;
        }
        return true;
    }
 
    public static RfnDevice of(RfnMeter rfnMeter) {
        return new RfnDevice(rfnMeter.getName(), rfnMeter, rfnMeter.getRfnIdentifier());
    }
}