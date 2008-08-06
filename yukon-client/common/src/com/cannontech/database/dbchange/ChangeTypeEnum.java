package com.cannontech.database.dbchange;

import com.cannontech.message.dispatch.message.DBChangeMsg;

public enum ChangeTypeEnum {
    ADD, UPDATE, DELETE;
    
    public static ChangeTypeEnum createFromDbChange(DBChangeMsg msg) {
        int typeOfChange = msg.getTypeOfChange();
        switch (typeOfChange) {
        case DBChangeMsg.CHANGE_TYPE_ADD: 
            return ADD;
        case DBChangeMsg.CHANGE_TYPE_UPDATE: 
            return UPDATE;
        case DBChangeMsg.CHANGE_TYPE_DELETE: 
            return DELETE;
        default:
            throw new IllegalArgumentException("Invalid change type: " + typeOfChange);
        }
    }
}
