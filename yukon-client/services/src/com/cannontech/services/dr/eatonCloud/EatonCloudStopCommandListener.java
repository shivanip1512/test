package com.cannontech.services.dr.eatonCloud;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.eatonCloud.EatonCloudMessageListener;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EatonCloudStopCommandListener {
    private final static Logger log = YukonLogManager.getLogger(EatonCloudStopCommandListener.class);
    
    @Autowired EatonCloudMessageListener eatonCloudMessageListener;
    @Autowired ThriftByteDeserializer<LMEatonCloudStopCommand> eatonCloudStopCommandSerializer;
    
    public void handleMessage(byte[] message) {
        var command = eatonCloudStopCommandSerializer.fromBytes(message);
        log.debug("LMEatonCloudStopCommand message recieved: {}", command);
        
        eatonCloudMessageListener.handleRestoreMessage(command.getGroupId(), command.getRestoreTime());
    }

}
