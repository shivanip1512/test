package com.cannontech.clientutils.commander;

import java.awt.Color;

import com.cannontech.common.YukonColorPalette;

public enum MessageType {
    
    INFO(YukonColorPalette.BLACK), 
    ERROR(YukonColorPalette.RED), 
    SUCCESS(YukonColorPalette.BLUE), 
    INHIBITED(YukonColorPalette.GRAY);
    
    private YukonColorPalette color;
    
    private MessageType(YukonColorPalette color) {
        this.color = color;
    }
    
    public YukonColorPalette getColor() {
        return color;
    }
    
    public String getStyleString() {
        return getColor().toDefaultText();
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