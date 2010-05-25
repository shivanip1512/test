package com.cannontech.dr.program.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.model.DrActionStateEnum;

public class Program extends ControllablePao {

    public Program(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }

    @Override
    public DrActionStateEnum getDrActionState() {
        return DrActionStateEnum.CONTROLLABLE;
    }

}