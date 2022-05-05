package com.cannontech.web.derEdge.service;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;

public interface DerEdgeCommunicationService {

    public short sendUnicastRequest(YukonPao pao, byte[] payload, YukonUserContext userContext);
    
}
