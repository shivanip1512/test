package com.cannontech.web.api.route.service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.web.api.route.model.RouteBaseModel;

public interface RouteHelper {

    /**
     * To retrieve routeType using SignalTramitter Id
     * 
     * @param id
     * @return
     * @throws NotFoundException
     */

    PaoType getRouteType(String id) throws NotFoundException;

    /**
     * Retrieves PaoType from Cache based on id provided.
     */
    PaoType getPaoTypeFromCache(String id);

    /**
     * Retrieves RouteModel from RouteFactoryModel based on RouteType provided.
     */
    RouteBaseModel getRouteFromModelFactory(PaoType paoType);
}
