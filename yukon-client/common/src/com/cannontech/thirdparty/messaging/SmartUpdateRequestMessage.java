package com.cannontech.thirdparty.messaging;

import java.io.Serializable;

import com.cannontech.common.pao.PaoIdentifier;

public class SmartUpdateRequestMessage implements Serializable{
    private static final long serialVersionUID = 1L;
    private PaoIdentifier paoIdentifier;
    
    public SmartUpdateRequestMessage(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier.getPaoIdentifier();
    }
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
}
