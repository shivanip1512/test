package com.cannontech.web.api.route.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.route.RouteBase;

public class RouteModelFactory {

    public static RouteBaseModel<? extends RouteBase> getModel(PaoType paoType) {
        RouteBaseModel routeBaseModel = null;

        switch (paoType) {
        case ROUTE_MACRO:
        case ROUTE_TCU:
        case ROUTE_LCU:
        case ROUTE_VERSACOM:
        case ROUTE_TAP_PAGING:
        case ROUTE_TNPP_TERMINAL:
        case ROUTE_WCTP_TERMINAL:
        case ROUTE_SNPP_TERMINAL:
        case ROUTE_SERIES_5_LMI:
        case ROUTE_RTC:
        case ROUTE_RDS_TERMINAL:
            routeBaseModel = new NonCCURouteModel();
            break;
        case ROUTE_CCU:
            routeBaseModel = new CCURouteModel();
            break;
        default:
            break;
        }
        return routeBaseModel;
    }
}
