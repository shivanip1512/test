package com.cannontech.web.api.der.edge.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.edgeDr.EdgeBroadcastMessagePriority;
import com.cannontech.dr.edgeDr.EdgeDrCommunicationException;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.user.YukonUserContext;

public interface DerEdgeCommunicationService {

    String sendUnicastRequest(YukonPao pao, byte[] payload, EdgeUnicastPriority queuePriority,
            EdgeUnicastPriority networkPriority, YukonUserContext userContext) throws EdgeDrCommunicationException;

    String sendMultiUnicastRequest(Set<SimpleDevice> simpleDeviceList, byte[] payload, EdgeUnicastPriority queuePriority,
            EdgeUnicastPriority networkPriority, YukonUserContext userContext) throws EdgeDrCommunicationException;

    void sendBroadcastRequest(byte[] payload, EdgeBroadcastMessagePriority priority, YukonUserContext userContext) throws EdgeDrCommunicationException;

    void cacheE2EIDToGUIDResponses(Map<Integer, Short> paoToE2eId, String messageGuid) throws EdgeDrCommunicationException;

}
