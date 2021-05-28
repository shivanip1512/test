package com.cannontech.web.api.route.model;

import com.cannontech.common.pao.PaoType;

public class RouteModelFactory {

    public static RouteBaseModel getModel(PaoType paoType) {
        RouteBaseModel routeBaseModel = null;

        switch (paoType) {
        case ROUTE_CCU:
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
            routeBaseModel = new RouteBaseModel();
        default:
            break;
        }
        return routeBaseModel;
    }
}
