package com.cannontech.database.data.stars.appliance;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AirConditioner extends ApplianceBase {

    private com.cannontech.database.db.stars.appliance.ApplianceAirConditioner airConditioner = null;

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
        getAirConditioner().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
        super.add();
        getAirConditioner().add();
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getAirConditioner().update();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getAirConditioner().retrieve();
    }

    public com.cannontech.database.db.stars.appliance.ApplianceAirConditioner getAirConditioner() {
        if (airConditioner == null)
            airConditioner = new com.cannontech.database.db.stars.appliance.ApplianceAirConditioner();
        return airConditioner;
    }

    public void setAirConditioner(com.cannontech.database.db.stars.appliance.ApplianceAirConditioner newAirConditioner) {
        airConditioner = newAirConditioner;
    }

}