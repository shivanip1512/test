package com.cannontech.web.api.der.edge.service.impl;

import java.util.Arrays;

import org.apache.logging.log4j.core.Logger;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.EdgeBroadcastRequest.Priority;
import com.cannontech.web.api.der.edge.service.DerEdgeCommunicationService;

@Service
public class DerEdgeCommunicationServiceImpl implements DerEdgeCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(DerEdgeCommunicationServiceImpl.class);
    
    @Override
    public short sendUnicastRequest(YukonPao pao, byte[] payload, YukonUserContext userContext) {
        
        //TODO later - clean up this logging
        log.info("Processing DER Edge Unicast Request - Pao: {}, payload: {}", pao, Arrays.toString(payload));
        
        //TODO later - send request to porter and receive response
        
        return (short) 0; //TODO return real e2e ID
    }

    @Override
    public void sendBroadcastRequest(byte[] payload, Priority priority, YukonUserContext userContext) {
        
        //TODO later - clean up this logging
        log.info("Processing DER Edge Broadcastt Request - Payload: {}, Priority: {}", Arrays.toString(payload), priority);
        
        //TODO later - send request to porter and receive response
    }

}
