package com.cannontech.services.dr.eatonCloud;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.eatonCloud.EatonCloudMessageListener;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EatonCloudCycleScheduledCommandListener {
    private final static Logger log = YukonLogManager.getLogger(EatonCloudCycleScheduledCommandListener.class);
    
    @Autowired EatonCloudMessageListener eatonCloudMessageListener;
    @Autowired ThriftByteDeserializer<LMEatonCloudScheduledCycleCommand> eatonCloudCycleScheduledCommandSerializer;
    
    public void handleMessage(byte[] message) {
        var command = eatonCloudCycleScheduledCommandSerializer.fromBytes(message);
        log.debug("LMEatonCloudSchdeuledCycleCommand message recieved: {}", command);
        
        eatonCloudMessageListener.handleCyclingControlMessage(command.getGroupId(), command.getControlStartDateTime(), command.getControlEndDateTime(), command.getDutyCyclePercentage());
    }

}
