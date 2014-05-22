package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;

public class RTCRoute extends RouteBase {

    public RTCRoute() {
        super(PaoType.ROUTE_RTC);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }
}