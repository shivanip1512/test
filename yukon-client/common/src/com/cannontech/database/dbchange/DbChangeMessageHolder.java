package com.cannontech.database.dbchange;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.dispatch.message.DBChangeMsg;

public class DbChangeMessageHolder {
    private List<DBChangeMsg> dbChanges = new ArrayList<DBChangeMsg>();
    
    public void addDbChange(DBChangeMsg dbChangeMsg) {
        dbChanges.add(dbChangeMsg);
    }
    
    public List<DBChangeMsg> getDbChanges() {
        return dbChanges;
    }
}
