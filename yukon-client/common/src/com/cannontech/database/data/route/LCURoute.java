package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;

public class LCURoute extends RouteBase {

    public LCURoute() {
        super(PaoType.ROUTE_LCU);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }
}
