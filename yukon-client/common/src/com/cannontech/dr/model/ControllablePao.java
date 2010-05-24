package com.cannontech.dr.model;

import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;

public class ControllablePao extends DisplayablePaoBase {

    public ControllablePao(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }
    
    public DrActionStateEnum getDrActionState() {
        return DrActionStateEnum.CONTROLLABLE;
    }
    
}