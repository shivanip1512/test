package com.cannontech.web.contextualMenu;

import com.cannontech.common.pao.PaoIdentifier;

public interface MenuTypeApplicabilityDecider {
    boolean isApplicable(PaoIdentifier paoIdentifier);
}
