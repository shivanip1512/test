package com.cannontech.database.data.stars.report;

import com.cannontech.database.Transaction;
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
    private com.cannontech.database.data.stars.report.ServiceCompany serviceCompany = null;
    private com.cannontech.database.data.stars.customer.CustomerAccount customerAccount = null;
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

        if (getServiceCompany() == null) {
        	serviceCompany = new ServiceCompany();
        	serviceCompany.setCompanyID( getWorkOrderBase().getServiceCompanyID() );
        	serviceCompany.setDbConnection( getDbConnection() );
        	serviceCompany.retrieve();
        }

        if (getCustomerAccount() == null) {
        	customerAccount = new com.cannontech.database.data.stars.customer.CustomerAccount();
        	customerAccount.setAccountID( getWorkOrderBase().getAccountID() );
        	customerAccount.setDbConnection( getDbConnection() );
        	customerAccount.retrieve();
        }
    }
    
    public static void deleteAllWorkOrders(Integer accountID, java.sql.Connection conn) {
    	try {
	    	com.cannontech.database.db.stars.report.WorkOrderBase[] orders =
	    			com.cannontech.database.db.stars.report.WorkOrderBase.getAllWorkOrders( accountID );
	    	
	    	if (orders != null) {
	    		for (int i = 0; i < orders.length; i++) {
	    			WorkOrderBase order = new WorkOrderBase();
	    			order.setOrderID( orders[i].getOrderID() );
	    			order.setDbConnection( conn );
	    			order.delete();
	    		}
	    	}
    	}
    	catch (java.sql.SQLException e) {
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
	/**
	 * Returns the serviceCompany.
	 * @return com.cannontech.database.data.stars.report.ServiceCompany
	 */
	public com.cannontech.database.data.stars.report.ServiceCompany getServiceCompany() {
		return serviceCompany;
	}

	/**
	 * Sets the serviceCompany.
	 * @param serviceCompany The serviceCompany to set
	 */
	public void setServiceCompany(
		com.cannontech.database.data.stars.report.ServiceCompany serviceCompany) {
		this.serviceCompany = serviceCompany;
	}

	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Returns the customerAccount.
	 * @return com.cannontech.database.data.stars.customer.CustomerAccount
	 */
	public com.cannontech.database.data.stars.customer.CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * Sets the customerAccount.
	 * @param customerAccount The customerAccount to set
	 */
	public void setCustomerAccount(com.cannontech.database.data.stars.customer.CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

}