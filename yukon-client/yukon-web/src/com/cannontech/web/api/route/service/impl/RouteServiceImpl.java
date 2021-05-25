package com.cannontech.web.api.route.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.service.RouteService;
import com.cannontech.yukon.IDatabaseCache;

public class RouteServiceImpl implements RouteService {

    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoCreationHelper paoCreationHelper;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    public RouteBaseModel<? extends RouteBase> create(RouteBaseModel routeBaseModel, LiteYukonUser yukonUser) {

        setRoutePaoType(routeBaseModel);
        RouteBase routeBase = RouteFactory.createRoute(routeBaseModel.getType());
        routeBaseModel.buildDBPersistent(routeBase);
        dbPersistentDao.performDBChange(routeBase, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(routeBase.getPAObjectID(), routeBase.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        routeBaseModel.buildModel(routeBase);
        return routeBaseModel;
    }

    /**
     * Constructing the PaoType Instance
     */
    private void setRoutePaoType(RouteBaseModel<? extends RouteBase> routeBaseModel) {
        PaoType routePaoType = null;
        PaoType paoType = getPaoTypeFromCache(String.valueOf(routeBaseModel.getSignalTransmitterId()));
        if (paoType != null) {
            if (paoType.isCcu() || paoType.isRepeater()) {
                routePaoType = PaoType.ROUTE_CCU;
            } else if (paoType.isTcu()) {
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
            } else {
                throw new Error("paoType - Unknown transmitter type");
            }
        }
        routeBaseModel.setType(routePaoType);
    }

    /**
     * Retrieves PaoType from Cache based on id provided.
     */
    private PaoType getPaoTypeFromCache(String id) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id));
        if (pao == null) {
            throw new NotFoundException("Signal transmitter id " + id + " not found.");
        }
        return pao.getPaoType();
    }
}
