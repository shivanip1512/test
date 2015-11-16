package com.cannontech.web.contextualMenu;

import com.cannontech.common.pao.PaoIdentifier;

/**
 * This interface serves as a way for MenuEntry classes to add support for only adding
 * MenuEntry's that are supported based on a their PaoIdentifier
 */
public interface MenuTypeApplicabilityDecider {
    boolean isApplicable(PaoIdentifier paoIdentifier);
}
