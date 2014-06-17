package com.cannontech.database.data.route;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.database.db.route.RepeaterRoute;

public class CCURoute extends RouteBase {
    private CarrierRoute carrierRoute = null;
    private List<RepeaterRoute> repeaters = null;

    public CCURoute() {
        super(PaoType.ROUTE_CCU);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getCarrierRoute().add();

        for (RepeaterRoute repeater : getRepeaters()) {
            ((DBPersistent)repeater).add();
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        RepeaterRoute rArray[] = RepeaterRoute.getRepeaterRoutes(getRouteID(), getDbConnection());
        for (int i = 0; i < rArray.length; i++) {
            rArray[i].setDbConnection(getDbConnection());
            rArray[i].delete();
            rArray[i].setDbConnection(null);
        }

        getCarrierRoute().delete();

        super.delete();
    }

    public CarrierRoute getCarrierRoute() {
        if (carrierRoute == null) {
            carrierRoute = new CarrierRoute();
        }
        return carrierRoute;
    }

    public List<RepeaterRoute> getRepeaters() {

        // Make sure to instantiate all the repeaters we'll need
        // otherwise on a delete they will be skipped
        // and probably cause constraint violations

        if (repeaters == null) {
            repeaters = new ArrayList<RepeaterRoute>();
        }

        return repeaters;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getCarrierRoute().retrieve();

        // Need to get all of the rows.....
        repeaters = new ArrayList<RepeaterRoute>();

        try {
            RepeaterRoute rArray[] = RepeaterRoute.getRepeaterRoutes(getRouteID(), getDbConnection());
            for (int i = 0; i < rArray.length; i++) {
                repeaters.add(rArray[i]);
            }

        } catch (java.sql.SQLException e) {
            // not necessarily an error
        }

        for (RepeaterRoute repeater : getRepeaters()) {
            ((DBPersistent) repeater).setDbConnection(getDbConnection());
            ((DBPersistent) repeater).retrieve();
            ((DBPersistent) repeater).setDbConnection(null);

        }
    }

    public void setCarrierRoute(CarrierRoute newValue) {
        this.carrierRoute = newValue;
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getCarrierRoute().setDbConnection(conn);

        for (RepeaterRoute repeater : getRepeaters()) {
            ((DBPersistent) repeater).setDbConnection(conn);
        }
    }

    public void setRepeaters(List<RepeaterRoute> newValue) {
        this.repeaters = newValue;
    }

    @Override
    public void setRouteID(Integer routeID) {
        super.setRouteID(routeID);
        getCarrierRoute().setRouteID(routeID);

        for (RepeaterRoute repeater : getRepeaters()) {
            repeater.setRouteID(routeID);
        }
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getCarrierRoute().update();

        RepeaterRoute.deleteRepeaterRoutes(getRouteID(), getDbConnection());

        for (RepeaterRoute repeater : getRepeaters()) {
            ((DBPersistent) repeater).add();
        }
    }
}