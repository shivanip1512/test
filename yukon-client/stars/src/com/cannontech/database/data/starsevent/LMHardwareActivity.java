package com.cannontech.database.data.starsevent;

import com.cannontech.database.db.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.database.Transaction;

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
    private com.cannontech.database.db.starscustomer.CustomerAction customerAction = null;

    public LMHardwareActivity() {
        super();
    }

    public void setEventID(Integer newID) {
        getLMHardwareActivity().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMHardwareActivity().setDbConnection(conn);
        getCustomerAction().setDbConnection(conn);
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
        
        getCustomerAction().setActionID( getLMHardwareActivity().getActionID() );
        getCustomerAction().retrieve();
    }
    
    public static com.cannontech.database.db.starsevent.LMHardwareActivity getLastLMHardwareEvent(Integer invID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
			return com.cannontech.database.db.starsevent.LMHardwareActivity.getLastHardwareActivity( invID, conn );
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
    
    public static StarsLMHardwareHistory getStarsLMHardwareHistory(Integer invID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
	        com.cannontech.database.db.starsevent.LMHardwareActivity[] events =
	        		com.cannontech.database.db.starsevent.LMHardwareActivity.getAllHardwareActivities( invID, conn );
	        StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
	        
	        for (int i = 0; i < events.length; i++) {
	        	com.cannontech.database.db.starscustomer.CustomerAction action = new com.cannontech.database.db.starscustomer.CustomerAction();
	        	action.setActionID( events[i].getActionID() );
	        	Transaction transaction = Transaction.createTransaction( Transaction.RETRIEVE, action );
	        	transaction.execute();
	        	
	        	LMHardwareEvent hwEvent = new LMHardwareEvent();
	        	hwEvent.setEventAction( action.getAction() );
	        	hwEvent.setEventDateTime( events[i].getEventDateTime() );
	        	hwEvent.setNotes( events[i].getNotes() );
	        	hwHist.addLMHardwareEvent( hwEvent );
	        }
	        
	        return hwHist;
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

    public com.cannontech.database.db.starsevent.LMHardwareActivity getLMHardwareActivity() {
        if (_LMHardwareActivity == null)
            _LMHardwareActivity = new com.cannontech.database.db.starsevent.LMHardwareActivity();
        return _LMHardwareActivity;
    }

    public void setLMHardwareActivity(com.cannontech.database.db.starsevent.LMHardwareActivity newLMHardwareActivity) {
        _LMHardwareActivity = newLMHardwareActivity;
    }
	/**
	 * Returns the customerAction.
	 * @return com.cannontech.database.db.starscustomer.CustomerAction
	 */
	public com.cannontech.database.db.starscustomer.CustomerAction getCustomerAction() {
		if (customerAction == null)
			customerAction = new com.cannontech.database.db.starscustomer.CustomerAction();
		return customerAction;
	}

	/**
	 * Sets the customerAction.
	 * @param customerAction The customerAction to set
	 */
	public void setCustomerAction(
		com.cannontech.database.db.starscustomer.CustomerAction customerAction) {
		this.customerAction = customerAction;
	}

}