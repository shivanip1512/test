package com.cannontech.database.data.route;

import com.cannontech.common.pao.PaoType;

public class TCURoute extends RouteBase {

    public TCURoute() {
        super(PaoType.ROUTE_TCU);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }
}
