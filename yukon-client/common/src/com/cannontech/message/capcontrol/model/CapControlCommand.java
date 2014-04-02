package com.cannontech.message.capcontrol.model;

import java.util.Random;

import com.cannontech.message.util.Message;

public class CapControlCommand extends Message implements CommandResultParticipant {
    private final static Random random = new Random();

    private int commandId;
    private int messageId = random.nextInt();
    
    public CapControlCommand() {
        super();
    }
    
    public CapControlCommand(int commandId) {
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
