package com.cannontech.web.api.route.service.impl;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.model.RouteModelFactory;
import com.cannontech.web.api.route.service.RouteHelper;
import com.cannontech.yukon.IDatabaseCache;

public class RouteHelperImpl implements RouteHelper {

    private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);

    /**
     * Retrieve route type using Signal Transmitter Id
     */
    @Override
    public PaoType getRouteType(String id) {
        PaoType routePaoType = null;
        PaoType paoType = getPaoTypeFromCache(id);
        if (paoType != null) {
            if (paoType.isTcu()) {
                routePaoType = PaoType.ROUTE_TCU;
            } else if (paoType.isLcu()) {
                routePaoType = PaoType.ROUTE_LCU;
            } else if (paoType == PaoType.TAPTERMINAL) {
                routePaoType = PaoType.ROUTE_TAP_PAGING;
            } else if (paoType == PaoType.WCTP_TERMINAL) {
                routePaoType = PaoType.ROUTE_WCTP_TERMINAL;
            } else if (paoType == PaoType.SNPP_TERMINAL) {
                routePaoType = PaoType.ROUTE_SNPP_TERMINAL;
            } else if (paoType == PaoType.TNPP_TERMINAL) {
                routePaoType = PaoType.ROUTE_TNPP_TERMINAL;
            } else if (paoType == PaoType.RTC) {
                routePaoType = PaoType.ROUTE_RTC;
            } else if (paoType == PaoType.SERIES_5_LMI) {
                routePaoType = PaoType.ROUTE_SERIES_5_LMI;
            } else if (paoType == PaoType.RDS_TERMINAL) {
                routePaoType = PaoType.ROUTE_RDS_TERMINAL;
            } else if (paoType.isCcu()) {
                routePaoType = PaoType.ROUTE_CCU;
            } else if (paoType == PaoType.ROUTE_MACRO) {
                routePaoType = PaoType.ROUTE_MACRO;
            } else {
                throw new NotFoundException("signalTransmitterId " + id + " is not valid");
            }
        }
        return routePaoType;
    }

    /**
     * Retrieves PaoType from Cache based on id provided.
     */
    @Override
    public PaoType getPaoTypeFromCache(String id) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id));
        if (pao == null) {
            throw new NotFoundException("Signal transmitter id " + id + " not found.");
        }
        return pao.getPaoType();
    }

    /**
     * Retrieves RouteBaseModel from model factory.
     */
    @Override
    public RouteBaseModel getRouteFromModelFactory(PaoType paoType) {
        RouteBaseModel routeBaseModel = RouteModelFactory.getModel(paoType);
        if (routeBaseModel != null) {
            return routeBaseModel;
        } else {
            // throw exception for not supported pointType
            throw new TypeNotSupportedException(paoType + " type is not supported.");
        }
    }

}
