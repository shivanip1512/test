package com.cannontech.clientutils.commander.model;

import com.cannontech.clientutils.commander.MessageType;

public class OutputMessage {
    
    public MessageType messageType = MessageType.INFO;
    public static final int DISPLAY_MESSAGE = 0; // YC defined text
    public static final int DEBUG_MESSAGE = 1; // Porter defined text
    private int displayAreaType = DEBUG_MESSAGE;
    private String text;
    private boolean isUnderline = false;

    public OutputMessage(int displayAreaType, String text, MessageType messageType) {
        this(displayAreaType, text, messageType, false);
    }
    
    public OutputMessage(int displayAreaType, String text, MessageType messageType, boolean isUnderline) {
        super();
        this.displayAreaType = displayAreaType;
        this.text = text;
        this.text = this.text.replaceAll("\n", "<BR>");
        this.text = this.text.replaceAll("<BR><BR>", "<BR>");
        this.messageType = messageType;
        this.isUnderline = isUnderline;
    }
    
    public boolean isUnderline() {
        return isUnderline;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public String getText() {
        return text;
    }
    
    public int getDisplayAreaType() {
        return displayAreaType;
    }
    
}