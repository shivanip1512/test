package com.cannontech.database.data.starsevent;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareActivity extends DBPersistent {

    private com.cannontech.database.db.starsevent.LMHardwareActivity _LMHardwareActivity = null;

    public LMHardwareActivity() {
        super();
    }

    public void setEventID(Integer newID) {
        getLMHardwareActivity().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMHardwareActivity().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getLMHardwareActivity().delete();
    }

    public void add() throws java.sql.SQLException {
        getLMHardwareActivity().add();
    }

    public void update() throws java.sql.SQLException {
        getLMHardwareActivity().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getLMHardwareActivity().retrieve();
    }

    public com.cannontech.database.db.starsevent.LMHardwareActivity getLMHardwareActivity() {
        if (_LMHardwareActivity == null)
            _LMHardwareActivity = new com.cannontech.database.db.starsevent.LMHardwareActivity();
        return _LMHardwareActivity;
    }

    public void setLMHardwareActivity(com.cannontech.database.db.starsevent.LMHardwareActivity newLMHardwareActivity) {
        _LMHardwareActivity = newLMHardwareActivity;
    }
}