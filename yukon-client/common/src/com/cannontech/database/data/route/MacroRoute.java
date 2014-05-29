package com.cannontech.database.data.route;

import java.util.Vector;

import com.cannontech.common.pao.PaoType;

public class MacroRoute extends RouteBase {

    private Vector<com.cannontech.database.db.route.MacroRoute> macroRouteVector = null;

    public MacroRoute() {
        super(PaoType.ROUTE_MACRO);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        for (int i = 0; i < getMacroRouteVector().size(); i++) {
            getMacroRouteVector().elementAt(i).add();
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        com.cannontech.database.db.route.MacroRoute.deleteAllMacroRoutes(getRouteID(), getDbConnection());
        super.delete();
    }

    public Vector<com.cannontech.database.db.route.MacroRoute> getMacroRouteVector() {
        if (macroRouteVector == null) {
            macroRouteVector = new Vector<com.cannontech.database.db.route.MacroRoute>();
        }

        return macroRouteVector;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();

        macroRouteVector = new Vector<com.cannontech.database.db.route.MacroRoute>();

        try {

            com.cannontech.database.db.route.MacroRoute rArray[] = com.cannontech.database.db.route.MacroRoute.getMacroRoutes(getRouteID());

            for (int i = 0; i < rArray.length; i++) {
                // Since we are in the process of doing a retrieve
                // we need to make sure the new macro routes have a database
                // connection to use
                // otherwise we bomb below
                rArray[i].setDbConnection(getDbConnection());
                macroRouteVector.addElement(rArray[i]);
            }

        } catch (java.sql.SQLException e) {
            // not necessarily an error
        }

        // This necessary??
        for (int i = 0; i < getMacroRouteVector().size(); i++) {
            getMacroRouteVector().elementAt(i).retrieve();
        }
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        Vector<com.cannontech.database.db.route.MacroRoute> v = getMacroRouteVector();

        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                v.elementAt(i).setDbConnection(conn);
            }
        }
    }

    public void setMacroRouteVector(Vector<com.cannontech.database.db.route.MacroRoute> newValue) {
        this.macroRouteVector = newValue;
    }

    @Override
    public void setRouteID(Integer routeID) {

        super.setRouteID(routeID);

        for (int i = 0; i < getMacroRouteVector().size(); i++) {
            getMacroRouteVector().elementAt(i).setRouteID(routeID);
        }
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();

        com.cannontech.database.db.route.MacroRoute.deleteAllMacroRoutes(getRouteID(), getDbConnection());

        for (int i = 0; i < getMacroRouteVector().size(); i++) {
            getMacroRouteVector().elementAt(i).add();
        }
    }
}