package com.cannontech.database.data.stars.report;

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
    private com.cannontech.database.db.stars.CustomerListEntry workType = null;
    private com.cannontech.database.db.stars.CustomerListEntry currentState = null
    ;
    private com.cannontech.database.data.stars.customer.AccountSite site = null;
    private com.cannontech.database.data.stars.report.ServiceCompany serviceCompany = null;
    private com.cannontech.database.data.stars.customer.CustomerBase customerBase = null;

    public WorkOrderBase() {
        super();
    }

    public void setOrderID(Integer newID) {
        getWorkOrderBase().setOrderID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getWorkOrderBase().setDbConnection(conn);
        getWorkType().setDbConnection(conn);
        getCurrentState().setDbConnection(conn);
        getSite().setDbConnection(conn);
        getServiceCompany().setDbConnection(conn);
        getCustomerBase().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from mapping table
        delete( "ECToWorkOrderMapping", "OrderID", getWorkOrderBase().getOrderID() );

        getWorkOrderBase().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getCustomerBase() == null)
    		throw new java.sql.SQLException("Add: setCustomerBase() must be called before this function");
    		
        getWorkOrderBase().add();
        
        if (getCustomerBase().getEnergyCompanyBase() != null) {
            // add to mapping table
            Object[] addValues = {
                getCustomerBase().getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
                getWorkOrderBase().getOrderID()
            };
            add( "ECToWorkOrderMapping", addValues );
        }
    }

    public void update() throws java.sql.SQLException {
        getWorkOrderBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getWorkOrderBase().retrieve();
        
        getWorkType().setEntryID( getWorkOrderBase().getWorkTypeID() );
        getWorkType().retrieve();
        
        getCurrentState().setEntryID( getWorkOrderBase().getCurrentStateID() );
        getCurrentState().retrieve();
        
        if (getSite().getAccountSite() == null) {
        	getSite().setAccountSiteID( getWorkOrderBase().getSiteID() );
        	getSite().retrieve();
        }
        
        if (getServiceCompany().getServiceCompany() == null) {
        	getServiceCompany().setCompanyID( getWorkOrderBase().getServiceCompanyID() );
        	getServiceCompany().retrieve();
        }
        
        if (getCustomerBase().getCustomerBase() == null) {
        	getCustomerBase().setCustomerID( getWorkOrderBase().getCustomerID() );
        	getCustomerBase().retrieve();
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
	 * Returns the currentState.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getCurrentState() {
		if (currentState == null)
			currentState = new com.cannontech.database.db.stars.CustomerListEntry();
		return currentState;
	}

	/**
	 * Returns the customerBase.
	 * @return com.cannontech.database.data.stars.customer.CustomerBase
	 */
	public com.cannontech.database.data.stars.customer.CustomerBase getCustomerBase() {
		if (customerBase == null)
			customerBase = new com.cannontech.database.data.stars.customer.CustomerBase();
		return customerBase;
	}

	/**
	 * Returns the serviceCompany.
	 * @return com.cannontech.database.data.stars.report.ServiceCompany
	 */
	public com.cannontech.database.data.stars.report.ServiceCompany getServiceCompany() {
		if (serviceCompany == null)
			serviceCompany = new ServiceCompany();
		return serviceCompany;
	}

	/**
	 * Returns the site.
	 * @return com.cannontech.database.data.stars.customer.AccountSite
	 */
	public com.cannontech.database.data.stars.customer.AccountSite getSite() {
		if (site == null)
			site = new com.cannontech.database.data.stars.customer.AccountSite();
		return site;
	}

	/**
	 * Returns the workType.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getWorkType() {
		if (workType == null)
			workType = new com.cannontech.database.db.stars.CustomerListEntry();
		return workType;
	}

	/**
	 * Sets the currentState.
	 * @param currentState The currentState to set
	 */
	public void setCurrentState(
		com.cannontech.database.db.stars.CustomerListEntry currentState) {
		this.currentState = currentState;
	}

	/**
	 * Sets the customerBase.
	 * @param customerBase The customerBase to set
	 */
	public void setCustomerBase(
		com.cannontech.database.data.stars.customer.CustomerBase customerBase) {
		this.customerBase = customerBase;
	}

	/**
	 * Sets the serviceCompany.
	 * @param serviceCompany The serviceCompany to set
	 */
	public void setServiceCompany(
		com.cannontech.database.data.stars.report.ServiceCompany serviceCompany) {
		this.serviceCompany = serviceCompany;
	}

	/**
	 * Sets the site.
	 * @param site The site to set
	 */
	public void setSite(
		com.cannontech.database.data.stars.customer.AccountSite site) {
		this.site = site;
	}

	/**
	 * Sets the workType.
	 * @param workType The workType to set
	 */
	public void setWorkType(
		com.cannontech.database.db.stars.CustomerListEntry workType) {
		this.workType = workType;
	}

}