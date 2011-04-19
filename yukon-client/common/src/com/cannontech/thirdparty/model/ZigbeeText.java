package com.cannontech.thirdparty.model;

public class ZigbeeText {

    /*
    * messageId should be unique 
    * duration is in minutes, and indicates how long the message will display for.
    * startTime is a 32-bit UTC (2000) specifying when the message will display. 0 means immediate.
    * confirmation of true will make the user confirm the message, false will not.
    * message is the String containing the message to be displayed.
    */
    
    private int messageId;
    private short duration;
    private int startTime;
    private boolean confirmation;
    private String message;
    
    public int getMessageId() {
        return messageId;
    }
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    public short getDuration() {
        return duration;
    }
    public void setDuration(short duration) {
        this.duration = duration;
    }
    public int getStartTime() {
        return startTime;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    public boolean isConfirmation() {
        return confirmation;
    }
    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
