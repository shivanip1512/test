package com.cannontech.common.csvImport.types;

import com.cannontech.common.pao.PaoType;

/**
 * Enum used for import, representing the regulator types.
 */
public enum RegulatorType {
    LTC(PaoType.LOAD_TAP_CHANGER),
    GANG_OPERATED(PaoType.GANG_OPERATED),
    PHASE_OPERATED(PaoType.PHASE_OPERATED),
    ;
    
    private PaoType paoType;
    
    private RegulatorType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
}
