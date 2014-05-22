package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;

public class WCTPTerminalRoute extends RouteBase {

    public WCTPTerminalRoute() {
        super(PaoType.ROUTE_WCTP_TERMINAL);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }
}
