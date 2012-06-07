package com.cannontech.stars.database.data.report;

import java.util.ArrayList;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.database.data.event.EventWorkOrder;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class WorkOrderBase extends DBPersistent {

    private com.cannontech.stars.database.db.report.WorkOrderBase workOrderBase = null;
    private Integer energyCompanyID = null;
    private ArrayList<EventWorkOrder> eventWorkOrders = null;

    public WorkOrderBase() {
        super();
    }

    public void setOrderID(Integer newID) {
        getWorkOrderBase().setOrderID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        for (int i = 0; i < getEventWorkOrders().size(); i++)
        	getEventWorkOrders().get(i).setDbConnection(conn);
        getWorkOrderBase().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from mapping table
        delete( "ECToWorkOrderMapping", "WorkOrderID", getWorkOrderBase().getOrderID() );

        EventWorkOrder.deleteEventWorkOrders(getWorkOrderBase().getOrderID().intValue());
        
        getWorkOrderBase().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    		
        getWorkOrderBase().add();
        
        for( int i = 0; i < getEventWorkOrders().size(); i++)
        	getEventWorkOrders().get(i).add();
        
        // add to mapping table
        Object[] addValues = {
            getEnergyCompanyID(),
            getWorkOrderBase().getOrderID()
        };
        add( "ECToWorkOrderMapping", addValues );
    }

    public void update() throws java.sql.SQLException {
    	getWorkOrderBase().update();
    }

    public void retrieve() throws java.sql.SQLException {

        getWorkOrderBase().retrieve();
        
        setEventWorkOrders(EventWorkOrder.retrieveEventWorkOrders(getWorkOrderBase().getOrderID().intValue()));
        
    	String[] SETTER_COLUMNS = {"EnergyCompanyID"};
    	String[] CONSTRAINT_COLUMNS = { "WorkOrderID" };
    	Object[] constraintValues = { getWorkOrderBase().getOrderID() };

        Object[] results = retrieve( SETTER_COLUMNS, "ECToWorkOrderMapping", CONSTRAINT_COLUMNS, constraintValues );
        if (results.length == SETTER_COLUMNS.length) {
            setEnergyCompanyID( (Integer) results[0] );
        }
    }
    
    public static void deleteAllWorkOrders(int accountID) {
    	try {
	    	int[] orderIDs = com.cannontech.stars.database.db.report.WorkOrderBase.searchByAccountID(accountID);
	    	
	    	if (orderIDs != null) {
	    		for (int i = 0; i < orderIDs.length; i++) {
	    			WorkOrderBase order = new WorkOrderBase();
	    			order.setOrderID( new Integer(orderIDs[i]) );
	    			
	    			Transaction.createTransaction( Transaction.DELETE, order ).execute();
	    		}
	    	}
    	}
    	catch (TransactionException e) {
    		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    	}
    }
    
    public com.cannontech.stars.database.db.report.WorkOrderBase getWorkOrderBase() {
        if (workOrderBase == null)
            workOrderBase = new com.cannontech.stars.database.db.report.WorkOrderBase();
        return workOrderBase;
    }

    public void setWorkOrderBase(com.cannontech.stars.database.db.report.WorkOrderBase newWorkOrderBase) {
        workOrderBase = newWorkOrderBase;
    }

	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	public ArrayList<EventWorkOrder> getEventWorkOrders() {
		if( eventWorkOrders == null)
			eventWorkOrders = new ArrayList<EventWorkOrder>();
		return eventWorkOrders;
	}

	public void setEventWorkOrders(ArrayList<EventWorkOrder> eventWorkOrders) {
		this.eventWorkOrders = eventWorkOrders;
	}

}