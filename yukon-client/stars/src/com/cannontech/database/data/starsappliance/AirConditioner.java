package com.cannontech.database.data.starsappliance;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AirConditioner extends ApplianceBase {

    private com.cannontech.database.db.starsappliance.AirConditioner airConditioner = null;

    public AirConditioner() {
        super();
    }

    public void setApplianceID(Integer newID) {
        super.setApplianceID(newID);
        getAirConditioner().setApplianceID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getAirConditioner().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getApplianceBase().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
        super.add();
        getApplianceBase().add();
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getApplianceBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getApplianceBase().retrieve();
    }

    public com.cannontech.database.db.starsappliance.AirConditioner getAirConditioner() {
        if (airConditioner == null)
            airConditioner = new com.cannontech.database.db.starsappliance.AirConditioner();
        return airConditioner;
    }

    public void setAirConditioner(com.cannontech.database.db.starsappliance.AirConditioner newAirConditioner) {
        airConditioner = newAirConditioner;
    }
}