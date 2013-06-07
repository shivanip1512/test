package com.cannontech.database.dbchange;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public class DbChangeMessageHolder {
    private List<DBChangeMessage> dbChanges = new ArrayList<DBChangeMessage>();
    
    public void addDbChange(DBChangeMessage dbChangeMsg) {
        dbChanges.add(dbChangeMsg);
    }
    
    public List<DBChangeMessage> getDbChanges() {
        return dbChanges;
    }
}
