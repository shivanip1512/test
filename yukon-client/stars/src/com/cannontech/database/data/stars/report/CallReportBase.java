package com.cannontech.database.data.stars.report;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CallReportBase extends DBPersistent {

    private com.cannontech.database.db.stars.report.CallReportBase callReportBase = null;
    private Integer energyCompanyID = null;

    public CallReportBase() {
        super();
    }

    public void setCallID(Integer newID) {
        getCallReportBase().setCallID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getCallReportBase().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from mapping table
        delete( "ECToCallReportMapping", "CallReportID", getCallReportBase().getCallID() );

        getCallReportBase().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (energyCompanyID == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    		
        getCallReportBase().add();
        
        // add to mapping table
        Object[] addValues = {
            energyCompanyID,
            getCallReportBase().getCallID()
        };
        add( "ECToCallReportMapping", addValues );
    }

    public void update() throws java.sql.SQLException {
        getCallReportBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getCallReportBase().retrieve();
    }
    
    public static void deleteAllCallReports(Integer accountID) {
    	try {
	    	com.cannontech.database.db.stars.report.CallReportBase[] calls =
	    			com.cannontech.database.db.stars.report.CallReportBase.getAllCallReports( accountID );
	    	
	    	if (calls != null) {
	    		for (int i = 0; i < calls.length; i++) {
	    			CallReportBase call = new CallReportBase();
	    			call.setCallID( calls[i].getCallID() );
	    			
	    			Transaction.createTransaction( Transaction.DELETE, call ).execute();
	    		}
	    	}
    	}
    	catch (TransactionException e) {
    		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    	}
    }
    
	/**
	 * Returns the callReportBase.
	 * @return com.cannontech.database.db.stars.report.CallReportBase
	 */
	public com.cannontech.database.db.stars.report.CallReportBase getCallReportBase() {
		if (callReportBase == null)
			callReportBase = new com.cannontech.database.db.stars.report.CallReportBase();
		return callReportBase;
	}

	/**
	 * Sets the callReportBase.
	 * @param callReportBase The callReportBase to set
	 */
	public void setCallReportBase(
		com.cannontech.database.db.stars.report.CallReportBase callReportBase) {
		this.callReportBase = callReportBase;
	}

	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}