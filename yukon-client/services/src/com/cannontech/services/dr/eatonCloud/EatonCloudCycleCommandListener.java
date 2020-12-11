package com.cannontech.services.dr.eatonCloud;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.eatonCloud.EatonCloudMessageListener;
import com.cannontech.loadcontrol.messages.LMEatonCloudCycleCommand;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EatonCloudCycleCommandListener {
    private final static Logger log = YukonLogManager.getLogger(EatonCloudCycleCommandListener.class);
    
    @Autowired EatonCloudMessageListener eatonCloudMessageListener;
    @Autowired ThriftByteDeserializer<LMEatonCloudCycleCommand> eatonCloudCycleCommandSerializer;

    public void handleMessage(byte[] message) {
        var command = eatonCloudCycleCommandSerializer.fromBytes(message);
        log.debug("LMEatonCloudCycleCommand message recieved: {}", command);
        
        Instant endTime = command.getCurrentDateTime().plus(command.getControlSeconds() * 1000);
        eatonCloudMessageListener.handleCyclingControlMessage(command.getGroupId(), command.getCurrentDateTime(), endTime, command.getDutyCyclePercentage());
    }

}
