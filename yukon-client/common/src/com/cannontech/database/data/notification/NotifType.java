package com.cannontech.database.data.notification;

public enum NotifType {
    EMAIL(0), VOICE(1), SMS(2);
    
    private int attribPosition;

    NotifType(int attribPosition) {
        this.attribPosition = attribPosition;
    }
    
    public int getAttribPosition() {
        return attribPosition;
    }
}
