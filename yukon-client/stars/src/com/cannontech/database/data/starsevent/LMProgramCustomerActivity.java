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

public class LMProgramCustomerActivity extends DBPersistent {

    private com.cannontech.database.db.starsevent.LMProgramCustomerActivity _LMProgramCustomerActivity = null;

    public LMProgramCustomerActivity() {
        super();
    }

    public void setEventID(Integer newID) {
        getLMProgramCustomerActivity().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMProgramCustomerActivity().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getLMProgramCustomerActivity().delete();
    }

    public void add() throws java.sql.SQLException {
        getLMProgramCustomerActivity().add();
    }

    public void update() throws java.sql.SQLException {
        getLMProgramCustomerActivity().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getLMProgramCustomerActivity().retrieve();
    }

    public com.cannontech.database.db.starsevent.LMProgramCustomerActivity getLMProgramCustomerActivity() {
        if (_LMProgramCustomerActivity == null)
            _LMProgramCustomerActivity = new com.cannontech.database.db.starsevent.LMProgramCustomerActivity();
        return _LMProgramCustomerActivity;
    }

    public void setLMProgramCustomerActivity(com.cannontech.database.db.starsevent.LMProgramCustomerActivity newLMProgramCustomerActivity) {
        _LMProgramCustomerActivity = newLMProgramCustomerActivity;
    }
}