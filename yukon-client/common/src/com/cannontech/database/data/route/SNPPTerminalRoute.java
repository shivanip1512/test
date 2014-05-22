package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;

public class SNPPTerminalRoute extends RouteBase {

    public SNPPTerminalRoute() {
        super(PaoType.ROUTE_SNPP_TERMINAL);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }
}
