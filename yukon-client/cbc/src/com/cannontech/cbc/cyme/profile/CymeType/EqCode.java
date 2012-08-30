package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;

public enum EqCode implements CymeRepresentation{
    CAPBANK("Shunt Capacitor", PaoType.CAPBANK),
    SUBSTATION("Substation",PaoType.CAP_CONTROL_SUBBUS);

    private String cymeValue;
    private PaoType paoType;
    
    private EqCode(String cymeValue, PaoType paoType) {
        this.cymeValue = cymeValue;
        this.paoType = paoType;
    }
    
    
    static public EqCode getFromCymeValue(String cymeValue) {
        for (EqCode eqCode : values()) {
            if (eqCode.getCymeValue().equals(cymeValue)) {
                return eqCode;
            }
        }
        throw new NotFoundException("EqCode not found for value: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return cymeValue;
    }

    public PaoType getPaoType() {
        return paoType;
    }
}
