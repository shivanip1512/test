package com.cannontech.web.contextualMenu;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public class PaoTypeMenuDecider implements MenuTypeApplicabilityDecider {

    private Set<PaoType> applicablePaoTypes;

    @Override
    public boolean isApplicable(PaoIdentifier paoIdentifier) {
        if (applicablePaoTypes == null) {
            return true;
        }
        return applicablePaoTypes.contains(paoIdentifier.getPaoType());
    }

    public void setApplicablePaoTypes(Set<PaoType> applicablePaoTypes) {
        this.applicablePaoTypes = applicablePaoTypes;
    }
}
