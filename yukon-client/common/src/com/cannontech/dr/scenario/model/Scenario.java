package com.cannontech.dr.scenario.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.model.DRActionStateEnum;

public class Scenario extends ControllablePao {

    private int programCount = 0;
    
    public Scenario(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }
    
    @Override
    public DRActionStateEnum getIsControllable() {
        if (programCount == 0) {
            return DRActionStateEnum.NO_ASSIGNED_PROGRAMS;
        } else {
            return DRActionStateEnum.CONTROLLABLE;
        }
    }
    
    public void setProgramCount(int programCount) {
        this.programCount = programCount;
    }

}