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

public class CallReportBase extends DBPersistent {

    private com.cannontech.database.db.stars.report.CallReportBase callReportBase = null;
    private com.cannontech.database.db.stars.CustomerListEntry callType = null;

    private com.cannontech.database.data.stars.customer.CustomerAccount customerAccount = null;

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
    	if (getCustomerAccount() == null)
    		throw new java.sql.SQLException("Add: setCustomerAccount() must be called before this function");
    		
        getCallReportBase().add();
        
        if (getCustomerAccount().getCustomerBase() != null && getCustomerAccount().getCustomerBase().getEnergyCompanyBase() != null) {
            // add to mapping table
            Object[] addValues = {
                getCustomerAccount().getCustomerBase().getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
                getCallReportBase().getCallID()
            };
            add( "ECToCallReportMapping", addValues );
        }
    }

    public void update() throws java.sql.SQLException {
        getCallReportBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getCallReportBase().retrieve();
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
	 * Returns the callType.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getCallType() {
		if (callType == null)
			callType = new com.cannontech.database.db.stars.CustomerListEntry();
		return callType;
	}

	/**
	 * Returns the customerAccount.
	 * @return com.cannontech.database.data.stars.customer.CustomerAccount
	 */
	public com.cannontech.database.data.stars.customer.CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * Sets the callReportBase.
	 * @param callReportBase The callReportBase to set
	 */
	public void setCallReportBase(
		com.cannontech.database.db.stars.report.CallReportBase callReportBase) {
		this.callReportBase = callReportBase;
	}

	/**
	 * Sets the callType.
	 * @param callType The callType to set
	 */
	public void setCallType(
		com.cannontech.database.db.stars.CustomerListEntry callType) {
		this.callType = callType;
	}

	/**
	 * Sets the customerAccount.
	 * @param customerAccount The customerAccount to set
	 */
	public void setCustomerAccount(
		com.cannontech.database.data.stars.customer.CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

}