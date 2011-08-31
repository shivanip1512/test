package com.cannontech.thirdparty.digi.dao.provider.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class DigiGatewayFields  implements PaoTemplatePart {
    private int digiId;
    
    public int getDigiId() {
        return digiId;
    }
    
    public void setDigiId(int digiId) {
        this.digiId = digiId;
    }
}
