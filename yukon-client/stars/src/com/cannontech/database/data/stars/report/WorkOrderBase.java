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

public class WorkOrderBase extends DBPersistent {

    private com.cannontech.database.db.stars.report.WorkOrderBase workOrderBase = null;
    private Integer energyCompanyID = null;

    public WorkOrderBase() {
        super();
    }

    public void setOrderID(Integer newID) {
        getWorkOrderBase().setOrderID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getWorkOrderBase().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from mapping table
        delete( "ECToWorkOrderMapping", "WorkOrderID", getWorkOrderBase().getOrderID() );

        getWorkOrderBase().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (energyCompanyID == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    		
        getWorkOrderBase().add();
        
        // add to mapping table
        Object[] addValues = {
            energyCompanyID,
            getWorkOrderBase().getOrderID()
        };
        add( "ECToWorkOrderMapping", addValues );
    }

    public void update() throws java.sql.SQLException {
        getWorkOrderBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getWorkOrderBase().retrieve();
    }
    
    public static void deleteAllWorkOrders(int accountID) {
    	try {
	    	int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByAccountID(accountID);
	    	
	    	if (orderIDs != null) {
	    		for (int i = 0; i < orderIDs.length; i++) {
	    			WorkOrderBase order = new WorkOrderBase();
	    			order.setOrderID( new Integer(orderIDs[i]) );
	    			
	    			Transaction.createTransaction( Transaction.DELETE, order ).execute();
	    		}
	    	}
    	}
    	catch (TransactionException e) {
    		e.printStackTrace();
    	}
    }
    
    public com.cannontech.database.db.stars.report.WorkOrderBase getWorkOrderBase() {
        if (workOrderBase == null)
            workOrderBase = new com.cannontech.database.db.stars.report.WorkOrderBase();
        return workOrderBase;
    }

    public void setWorkOrderBase(com.cannontech.database.db.stars.report.WorkOrderBase newWorkOrderBase) {
        workOrderBase = newWorkOrderBase;
    }

	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}