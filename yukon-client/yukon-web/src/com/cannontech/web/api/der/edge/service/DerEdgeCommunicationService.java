package com.cannontech.web.api.der.edge.service;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.EdgeBroadcastRequest.Priority;

public interface DerEdgeCommunicationService {

    public short sendUnicastRequest(YukonPao pao, byte[] payload, YukonUserContext userContext);

    public void sendBroadcastRequest(byte[] payload, Priority priority, YukonUserContext userContext);

}
