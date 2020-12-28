package com.cannontech.services.dr.eatonCloud;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.eatonCloud.EatonCloudMessageListener;
import com.cannontech.loadcontrol.messages.LMEatonCloudCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EatonCloudCommandListener {
    private final static Logger log = YukonLogManager.getLogger(EatonCloudCommandListener.class);

    @Autowired EatonCloudMessageListener eatonCloudMessageListener;
    @Autowired ThriftByteDeserializer<LMEatonCloudStopCommand> eatonCloudStopCommandSerializer;
    @Autowired ThriftByteDeserializer<LMEatonCloudCycleCommand> eatonCloudCycleCommandSerializer;
    @Autowired ThriftByteDeserializer<LMEatonCloudScheduledCycleCommand> eatonCloudCycleScheduledCommandSerializer;

    public void handleEatonCloudStopCommand(byte[] message) {
        var command = eatonCloudStopCommandSerializer.fromBytes(message);
        log.debug("LMEatonCloudStopCommand message recieved: {}", command);

        eatonCloudMessageListener.handleRestoreMessage(command.getGroupId(), command.getRestoreTime());
    }

    public void handleEatonCloudCycleScheduledCommand(byte[] message) {
        var command = eatonCloudCycleScheduledCommandSerializer.fromBytes(message);
        log.debug("LMEatonCloudSchdeuledCycleCommand message recieved: {}", command);

        eatonCloudMessageListener.handleCyclingControlMessage(command.getGroupId(), command.getControlStartDateTime(),
                command.getControlEndDateTime(), command.getDutyCyclePercentage());
    }

    public void handleEatonCloudCycleCommand(byte[] message) {
        var command = eatonCloudCycleCommandSerializer.fromBytes(message);
        log.debug("LMEatonCloudCycleCommand message recieved: {}", command);

        Instant endTime = command.getCurrentDateTime().plus(command.getControlSeconds() * 1000);
        eatonCloudMessageListener.handleCyclingControlMessage(command.getGroupId(), command.getCurrentDateTime(), endTime,
                command.getDutyCyclePercentage());
    }

}
