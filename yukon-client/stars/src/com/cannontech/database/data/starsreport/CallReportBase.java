package com.cannontech.database.data.starsreport;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CallReportBase extends DBPersistent {

    private com.cannontech.database.db.starsreport.CallReportBase callReportBase = null;

    private com.cannontech.database.data.starscustomer.CustomerBase customerBase = null;

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
        delete( "ECToServiceOrderMapping", "CallReportID", getCallReportBase().getCallID() );

        getCallReportBase().delete();
    }

    public void add() throws java.sql.SQLException {
        getCallReportBase().add();
        
        // setCustomerBase() should already be called
        if (getCustomerBase() != null && getCustomerBase().getEnergyCompanyBase() != null) {
            // add to mapping table
            Object[] addValues = {
                getCustomerBase().getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
                getCallReportBase().getCallID()
            };
            add( "ECToServiceOrderMapping", addValues );
        }
    }

    public void update() throws java.sql.SQLException {
        getCallReportBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getCallReportBase().retrieve();

        if (getCustomerBase() == null) {
            // retrieve CustomerBase
            com.cannontech.database.data.starscustomer.CustomerBase customer = new com.cannontech.database.data.starscustomer.CustomerBase();
            customer.setCustomerID( getCallReportBase().getCustomerID() );
            customer.setDbConnection( getDbConnection() );
            customer.retrieve();

            setCustomerBase( customer );
        }
    }

    public com.cannontech.database.db.starsreport.CallReportBase getCallReportBase() {
        if (callReportBase == null)
            callReportBase = new com.cannontech.database.db.starsreport.CallReportBase();
        return callReportBase;
    }

    public void setCallReportBase(com.cannontech.database.db.starsreport.CallReportBase newCallReportBase) {
        callReportBase = newCallReportBase;
    }

    public com.cannontech.database.data.starscustomer.CustomerBase getCustomerBase() {
        return customerBase;
    }

    public void setCustomerBase(com.cannontech.database.data.starscustomer.CustomerBase newCustomerBase) {
        customerBase = newCustomerBase;
    }
}