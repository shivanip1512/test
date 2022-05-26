package com.cannontech.web.api.der.edge.service;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.user.YukonUserContext;

public interface DerEdgeCommunicationService {

    public short sendUnicastRequest(YukonPao pao, byte[] payload, EdgeUnicastPriority queuePriority, 
            EdgeUnicastPriority networkPriority, YukonUserContext userContext);
    
}
