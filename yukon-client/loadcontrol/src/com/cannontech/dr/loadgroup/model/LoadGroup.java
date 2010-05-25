package com.cannontech.dr.loadgroup.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.model.DrActionStateEnum;

public class LoadGroup extends ControllablePao {

    public LoadGroup(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }

    @Override
    public DrActionStateEnum getDrActionState() {
        return DrActionStateEnum.CONTROLLABLE;
    }
}