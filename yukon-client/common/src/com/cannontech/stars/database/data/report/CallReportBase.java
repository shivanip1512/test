package com.cannontech.stars.database.data.report;

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

    private com.cannontech.stars.database.db.report.CallReportBase callReportBase = null;
    private Integer energyCompanyID = null;

    public CallReportBase() {
        super();
    }

    public void setCallID(Integer newID) {
        getCallReportBase().setCallID(newID);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getCallReportBase().setDbConnection(conn);
    }

    @Override
    public void delete() throws java.sql.SQLException {
        // delete from mapping table
        delete( "ECToCallReportMapping", "CallReportID", getCallReportBase().getCallID() );

        getCallReportBase().delete();
    }

    @Override
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

    @Override
    public void update() throws java.sql.SQLException {
        getCallReportBase().update();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        getCallReportBase().retrieve();
    }
    
	/**
	 * Returns the callReportBase.
	 * @return com.cannontech.stars.database.db.report.CallReportBase
	 */
	public com.cannontech.stars.database.db.report.CallReportBase getCallReportBase() {
		if (callReportBase == null)
			callReportBase = new com.cannontech.stars.database.db.report.CallReportBase();
		return callReportBase;
	}

	/**
	 * Sets the callReportBase.
	 * @param callReportBase The callReportBase to set
	 */
	public void setCallReportBase(
		com.cannontech.stars.database.db.report.CallReportBase callReportBase) {
		this.callReportBase = callReportBase;
	}

	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}