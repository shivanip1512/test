package com.cannontech.database.data.notification;

public enum NotifType {
    EMAIL(0,"email"),
    VOICE(1,"voice"),
    SMS(2,"sms");
    
    private int attribPosition;
    private String oldName;
    
    private NotifType(int attribPosition, String oldName) {
        this.attribPosition = attribPosition;
        this.oldName = oldName;
    }
    
    public int getAttribPosition() {
        return attribPosition;
    }
    
    public String getOldName() {
        return oldName;
    }
}
