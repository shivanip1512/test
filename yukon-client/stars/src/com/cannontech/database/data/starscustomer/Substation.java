package com.cannontech.database.data.starscustomer;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Substation extends DBPersistent {

    private com.cannontech.database.db.starscustomer.Substation substation = null;

    public Substation() {
        super();
    }

    public void setSubstationID(Integer newID) {
        getSubstation().setSubstationID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getSubstation().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        com.cannontech.database.db.starscustomer.SiteInformation.clearSubstation(
            getSubstation().getSubstationID(), getDbConnection() );
        getSubstation().delete();
    }

    public void add() throws java.sql.SQLException {
        getSubstation().add();
    }

    public void update() throws java.sql.SQLException {
        getSubstation().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getSubstation().retrieve();
    }

    public com.cannontech.database.db.starscustomer.Substation getSubstation() {
        if (substation == null)
            substation = new com.cannontech.database.db.starscustomer.Substation();
        return substation;
    }

    public void setSubstation(com.cannontech.database.db.starscustomer.Substation newSubstation) {
        substation = newSubstation;
    }
}