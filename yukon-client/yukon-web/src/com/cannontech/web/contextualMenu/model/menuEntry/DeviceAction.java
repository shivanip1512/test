package com.cannontech.web.contextualMenu.model.menuEntry;

import com.cannontech.common.pao.PaoIdentifier;

public interface DeviceAction extends MenuAction {
    /**
     * Returns a boolean indicating whether or not a single MenuEntry supports a passed in PaoIdentifier 
     * @param paoIdentifier
     */
    boolean supports(PaoIdentifier paoIdentifier);
}
