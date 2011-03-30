package com.cannontech.thirdparty.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.thirdparty.messaging.SepControlMessage;

public interface SepControlMessageHandler {
    
    public boolean handlePao(PaoIdentifier paoIdentifier);
    
    public void handleControlMessage(PaoIdentifier paoIdentifier, SepControlMessage message);
    
}
