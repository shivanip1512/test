package com.cannontech.dr.scenario.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.model.DrActionStateEnum;

public class Scenario extends ControllablePao {

    private int programCount = 0;
    
    public Scenario(PaoIdentifier paoIdentifier, String name, int programCount) {
        super(paoIdentifier, name);
        this.programCount = programCount;
    }
    
    @Override
    public DrActionStateEnum getDrActionState() {
        if (programCount == 0) {
            return DrActionStateEnum.NO_ASSIGNED_PROGRAMS;
        } else {
            return DrActionStateEnum.CONTROLLABLE;
        }
    }
}