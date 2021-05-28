package com.cannontech.web.api.route.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.web.api.route.model.RouteBaseModel;

public interface RouteService {

    public RouteBaseModel<? extends RouteBase> create(RouteBaseModel<? extends RouteBase> routeBaseModel,
            LiteYukonUser yukonUser);

    public RouteBaseModel<?> retrieve(int id);

    public List<RouteBaseModel> retrieveAllRoutes();

}
