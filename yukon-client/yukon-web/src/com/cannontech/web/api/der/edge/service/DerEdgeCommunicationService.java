package com.cannontech.web.api.der.edge.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.edgeDr.EdgeBroadcastMessagePriority;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.user.YukonUserContext;

public interface DerEdgeCommunicationService {

    Map<Integer, Short> sendUnicastRequest(YukonPao pao, byte[] payload, EdgeUnicastPriority queuePriority,
            EdgeUnicastPriority networkPriority, YukonUserContext userContext);

    Map<Integer, Short> sendMultiUnicastRequest(Set<SimpleDevice> simpleDeviceList, byte[] payload, EdgeUnicastPriority queuePriority,
            EdgeUnicastPriority networkPriority, YukonUserContext userContext);

    void sendBroadcastRequest(byte[] payload, EdgeBroadcastMessagePriority priority, YukonUserContext userContext);

}
