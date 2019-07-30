package com.cannontech.database.model;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public class WebOnlyEditWarning {

    private LiteYukonPAObject pao;
    
    public WebOnlyEditWarning(LiteYukonPAObject pao) {
        this.pao = pao;
    }
    
    public String getWarning() {
        return pao.getPaoType().getPaoTypeName() + " objects can only be edited using the Yukon Web interface.";
    }
    
    @Override
    public String toString() {
        return pao.getPaoName();
    }
}
