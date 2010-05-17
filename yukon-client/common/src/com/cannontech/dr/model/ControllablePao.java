package com.cannontech.dr.model;

import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;

public class ControllablePao extends DisplayablePaoBase {
    private DRActionStateEnum isControllable = DRActionStateEnum.CONTROLLABLE;
        
    public ControllablePao(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }
    
    public DRActionStateEnum getIsControllable() {
        return this.isControllable;
    }
    
}