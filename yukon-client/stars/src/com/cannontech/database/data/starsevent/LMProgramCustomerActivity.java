package com.cannontech.database.data.starsevent;

import com.cannontech.database.db.DBPersistent;


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
    private com.cannontech.database.db.starscustomer.CustomerAction customerAction = null;

    public LMProgramCustomerActivity() {
        super();
    }

    public void setEventID(Integer newID) {
        getLMProgramCustomerActivity().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMProgramCustomerActivity().setDbConnection(conn);
        getCustomerAction().setDbConnection(conn);
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
        
        getCustomerAction().setActionID( getLMProgramCustomerActivity().getAccountID() );
        getCustomerAction().retrieve();
    }
    
    public static com.cannontech.database.db.starsevent.LMProgramCustomerActivity getLastCustomerActivity(Integer accountID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
			return com.cannontech.database.db.starsevent.LMProgramCustomerActivity.getLastCustomerActivity( accountID, conn );
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return null;
    }

    public com.cannontech.database.db.starsevent.LMProgramCustomerActivity getLMProgramCustomerActivity() {
        if (_LMProgramCustomerActivity == null)
            _LMProgramCustomerActivity = new com.cannontech.database.db.starsevent.LMProgramCustomerActivity();
        return _LMProgramCustomerActivity;
    }

    public void setLMProgramCustomerActivity(com.cannontech.database.db.starsevent.LMProgramCustomerActivity newLMProgramCustomerActivity) {
        _LMProgramCustomerActivity = newLMProgramCustomerActivity;
    }
    
	public com.cannontech.database.db.starscustomer.CustomerAction getCustomerAction() {
		if (customerAction == null)
			customerAction = new com.cannontech.database.db.starscustomer.CustomerAction();
		return customerAction;
	}

	public void setCustomerAction(
		com.cannontech.database.db.starscustomer.CustomerAction customerAction) {
		this.customerAction = customerAction;
	}

}