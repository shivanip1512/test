package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;

public final class RouteFactory {

    public final static RouteBase createRoute(PaoType paoType) {

        RouteBase returnRoute = null;

        switch (paoType) {
        case ROUTE_CCU:
            returnRoute = new CCURoute();
            break;
        case ROUTE_TCU:
            returnRoute = new TCURoute();
            break;
        case ROUTE_LCU:
            returnRoute = new LCURoute();
            break;
        case ROUTE_MACRO:
            returnRoute = new MacroRoute();
            returnRoute.setDeviceID(new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID));
            returnRoute.setDefaultRoute("N");
            break;
        case ROUTE_VERSACOM:
            returnRoute = new VersacomRoute();
            break;
        case ROUTE_TAP_PAGING:
            returnRoute = new TapPagingRoute();
            break;
        case ROUTE_TNPP_TERMINAL:
            returnRoute = new TNPPTerminalRoute();
            break;
        case ROUTE_WCTP_TERMINAL:
            returnRoute = new WCTPTerminalRoute();
            break;
        case ROUTE_SNPP_TERMINAL:
            returnRoute = new SNPPTerminalRoute();
            break;
        case ROUTE_SERIES_5_LMI:
            returnRoute = new Series5LMIRoute();
            break;
        case ROUTE_RTC:
            returnRoute = new RTCRoute();
            break;
        case ROUTE_RDS_TERMINAL:
            returnRoute = new RDSTerminalRoute();
            break;
        }

        return returnRoute;
    }
}
