package com.cannontech.database.data.starsreport;

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

    private com.cannontech.database.db.starsreport.WorkOrderBase workOrderBase = null;

    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

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
        getWorkOrderBase().add();
        
        // setEnergyCompanyBase() should already be called
        if (getEnergyCompanyBase() != null) {
            // add to mapping table
            Object[] addValues = {
                getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
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
    }

    public com.cannontech.database.db.starsreport.WorkOrderBase getWorkOrderBase() {
        if (workOrderBase == null)
            workOrderBase = new com.cannontech.database.db.starsreport.WorkOrderBase();
        return workOrderBase;
    }

    public void setWorkOrderBase(com.cannontech.database.db.starsreport.WorkOrderBase newWorkOrderBase) {
        workOrderBase = newWorkOrderBase;
    }

    public com.cannontech.database.data.company.EnergyCompanyBase getEnergyCompanyBase() {
        return energyCompanyBase;
    }

    public void setEnergyCompanyBase(com.cannontech.database.data.company.EnergyCompanyBase newEnergyCompanyBase) {
        energyCompanyBase = newEnergyCompanyBase;
    }
}