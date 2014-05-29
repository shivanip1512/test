package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;


public class VersacomRoute extends RouteBase {
    private com.cannontech.database.db.route.VersacomRoute versacomRoute = null;

    public VersacomRoute() {
        super(PaoType.ROUTE_VERSACOM);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getVersacomRoute().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getVersacomRoute().delete();
        super.delete();
    }

    public com.cannontech.database.db.route.VersacomRoute getVersacomRoute() {
        if (versacomRoute == null) {
            versacomRoute = new com.cannontech.database.db.route.VersacomRoute();
        }

        return versacomRoute;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getVersacomRoute().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getVersacomRoute().setDbConnection(conn);
    }

    @Override
    public void setRouteID(Integer id) {
        super.setRouteID(id);
        getVersacomRoute().setRouteID(id);
    }

    public void setVersacomRoute(com.cannontech.database.db.route.VersacomRoute newValue) {
        versacomRoute = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getVersacomRoute().update();
    }
}