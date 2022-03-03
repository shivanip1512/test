package com.cannontech.web.api.route.service;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.web.api.route.model.RouteBaseModel;

public interface RouteService {

    /**
     * Create the Route.
     */
    RouteBaseModel<? extends RouteBase> create(RouteBaseModel<? extends RouteBase> routeBaseModel, LiteYukonUser yukonUser);

    /**
     * Retrieve Route for passed routeId.
     */
    RouteBaseModel<?> retrieve(int id);

    /**
     * Retrieve All the Routes
     */
    List<RouteBaseModel> retrieveAllRoutes();

    /**
     * Delete the Route.
     */
    int delete(int id, LiteYukonUser yukonUser) throws SQLException;

    /**
     * Update the Route.
     */
    RouteBaseModel<? extends RouteBase> update(int id, RouteBaseModel routeBase, LiteYukonUser liteYukonUser);

}