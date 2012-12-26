package com.cannontech.web.contextualMenu;

import com.cannontech.common.pao.PaoIdentifier;

public class MeterMenuDecider implements MenuTypeApplicabilityDecider {

    @Override
    public boolean isApplicable(PaoIdentifier paoIdentifier) {
        return paoIdentifier.getPaoType().isMeter();
    }
}
