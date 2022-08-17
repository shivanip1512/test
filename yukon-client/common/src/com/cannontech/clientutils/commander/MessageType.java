package com.cannontech.clientutils.commander;

import java.awt.Color;

public enum MessageType {
    
    INFO(Color.BLACK, "Black"), 
    ERROR(Color.RED, "Red"), 
    SUCCESS(Color.BLUE, "Blue"), 
    INHIBITED(Color.GRAY, "Gray");
    
    private Color color;
    private String styleString;
    
    private MessageType(Color color, String styleString) {
        this.color = color;
        this.styleString = styleString;
    }
    
    public Color getColor() {
        return color;
    }
    
    public String getStyleString() {
        return styleString;
    }
    
    public static MessageType getMessageType(int status) {
        if (status == 83 || status == 85) {
            return MessageType.INHIBITED;
        } else if (status > 0) {
            // 0=success
            // 1="Not Normal" YUK-10411/TSSL-1230 changed 1 to an "error"
            // everything else > 0 considered an error
            return MessageType.ERROR;
        } else {
            return MessageType.SUCCESS;
        }
    }
}