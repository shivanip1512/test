package com.cannontech.database.data.route;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.route.Route;
import com.cannontech.spring.YukonSpringHook;

public class RouteBase extends com.cannontech.database.data.pao.YukonPAObject implements EditorPanel {
    private Route route = null;

    public RouteBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        if (getRoute().getRouteID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setRouteID(paoDao.getNextPaoId());
        }

        super.add();
        getRoute().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        deleteFromMacro();
        getRoute().delete();
        super.delete();
    }

    public void deleteFromMacro() throws java.sql.SQLException {
        com.cannontech.database.db.route.MacroRoute.deleteFromMacro(getRouteID(), getDbConnection());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof RouteBase) {
            return getRoute().getRouteID().equals(((RouteBase) obj).getRoute().getRouteID());
        } else {
            return super.equals(obj);
        }
    }

    public String getDefaultRoute() {
        return getRoute().getDefaultRoute();
    }

    public Integer getDeviceID() {
        return getRoute().getDeviceID();
    }

    private Route getRoute() {
        if (route == null)
            route = new Route();

        return route;
    }

    public Integer getRouteID() {
        return getRoute().getRouteID();
    }

    public String getRouteName() {
        return getYukonPAObject().getPaoName();
    }

    public final static String hasDevice(Integer routeID) {
        SqlStatement stmt = new SqlStatement("SELECT PAOName FROM YukonPAObject y, DeviceRoutes r" + 
                                            " WHERE r.RouteID=" + routeID + " AND r.DeviceID=y.PAObjectID",
                                            CtiUtilities.getDatabaseAlias());

        try {
            stmt.execute();
            if (stmt.getRowCount() > 0) {
                return stmt.getRow(0)[0].toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public final static String hasLoadGroup(Integer routeID) {
        SqlStatement expresscomStmt = new SqlStatement("SELECT PAOName FROM YukonPAObject y, LMGroupExpressCom lmg" + 
                                                        " WHERE lmg.RouteID = " + routeID + " AND lmg.LMGroupId = y.PAObjectID",
                                                        CtiUtilities.getDatabaseAlias());

        SqlStatement versacomStmt = new SqlStatement("SELECT PAOName FROM YukonPAObject y, LMGroupVersacom lmg" + 
                                                    " WHERE lmg.RouteID = " + routeID + " AND lmg.DeviceID = y.PAObjectID",
                                                    CtiUtilities.getDatabaseAlias());

        SqlStatement mctStmt = new SqlStatement("SELECT PAOName FROM YukonPAObject y, LMGroupMCT lmg" + 
                                                " WHERE lmg.RouteID = " + routeID + " AND lmg.DeviceID = y.PAObjectID",
                                                CtiUtilities.getDatabaseAlias());

        try {
            expresscomStmt.execute();
            if (expresscomStmt.getRowCount() > 0) {
                return expresscomStmt.getRow(0)[0].toString();
            }

            versacomStmt.execute();
            if (versacomStmt.getRowCount() > 0) {
                return versacomStmt.getRow(0)[0].toString();
            }

            mctStmt.execute();
            if (mctStmt.getRowCount() > 0) {
                return mctStmt.getRow(0)[0].toString();
            }

            return null;

        } catch (Exception e) {
            return null;
        }

    }

    public final static String hasRepeater(Integer routeID) {
        SqlStatement stmt = new SqlStatement("SELECT PAOName FROM YukonPAObject y, RepeaterRoute rr" + 
                                            " WHERE rr.RouteID = " + routeID + " AND rr.DeviceID = y.PAObjectID",
                                            CtiUtilities.getDatabaseAlias());

        try {
            stmt.execute();
            if (stmt.getRowCount() > 0) {
                return stmt.getRow(0)[0].toString();
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

    }

    public final static String inMacroRoute(Integer routeID) {
        SqlStatement stmt = new SqlStatement("SELECT PAOName FROM YukonPAObject y, MacroRoute r" + 
                                            " WHERE r.SingleRouteID=" + routeID + " AND r.RouteID=y.PAObjectID",
                                            CtiUtilities.getDatabaseAlias());

        try {
            stmt.execute();
            if (stmt.getRowCount() > 0) {
                return stmt.getRow(0)[0].toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        getRoute().retrieve();
        super.retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getRoute().setDbConnection(conn);
    }

    public void setDefaultRoute(String newDefRoute) {
        getRoute().setDefaultRoute(newDefRoute);
    }

    public void setDeviceID(Integer newDevID) {
        getRoute().setDeviceID(newDevID);
    }

    public void setRoute(Route newValue) {
        this.route = newValue;
    }

    public void setRouteID(Integer routeID) {
        super.setPAObjectID(routeID);

        getRoute().setRouteID(routeID);
    }

    public void setRouteName(String newRouteName) {
        getYukonPAObject().setPaoName(newRouteName);
    }

    @Override
    public String toString() {
        return getPAOName();
    }

    @Override
    public void update() throws java.sql.SQLException {
        getRoute().update();
        super.update();
    }
}