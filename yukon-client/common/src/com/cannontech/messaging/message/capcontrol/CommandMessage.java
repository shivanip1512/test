package com.cannontech.messaging.message.capcontrol;

import org.apache.commons.lang.math.RandomUtils;

import com.cannontech.messaging.message.BaseMessage;

public class CommandMessage extends BaseMessage implements CommandResultParticipant {

    private int commandId;
    private int messageId = RandomUtils.nextInt();

    public CommandMessage() {
        super();
    }

    public CommandMessage(int commandId) {
        super();
        this.commandId = commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public int getCommandId() {
        return commandId;
    }

    @Override
    public int getMessageId() {
        return messageId;
    }

    @Override
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

}
