package com.cannontech.web.api.route.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.model.RouteModelFactory;
import com.cannontech.web.api.route.service.RouteService;
import com.cannontech.yukon.IDatabaseCache;

public class RouteServiceImpl implements RouteService {

    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DBDeletionDao dbDeletionDao;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public RouteBaseModel<? extends RouteBase> retrieve(int routeId) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllRoutesMap().get(routeId);
        if (pao == null) {
            throw new NotFoundException("Route Id not found");
        }

        RouteBase routeBase = (RouteBase) dbPersistentDao.retrieveDBPersistent(pao);
        RouteBaseModel routeBaseModel = RouteModelFactory.getModel(pao.getPaoType());// new factory
        routeBaseModel.buildModel(routeBase);
        return routeBaseModel;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<RouteBaseModel> retrieveAllRoutes() {
        List<LiteYukonPAObject> listOfRoutes = serverDatabaseCache.getAllRoutes();
        List<RouteBaseModel> routeBaseModelList = new ArrayList();
        if (!CollectionUtils.isEmpty(listOfRoutes)) {
            listOfRoutes.forEach(yukonPAObject -> {
                RouteBase routeBase = (RouteBase) dbPersistentDao.retrieveDBPersistent(yukonPAObject);
                RouteBaseModel routeBaseModel = RouteModelFactory.getModel(yukonPAObject.getPaoType());
                routeBaseModel.buildModel(routeBase);
                routeBaseModelList.add(routeBaseModel);
            });
        }
        return routeBaseModelList;
    }

    @Override
    @Transactional
    public int delete(int routeId, LiteYukonUser liteYukonUser) throws SQLException {

        LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllRoutes().stream()
                .filter(route -> route.getLiteID() == routeId)
                .findFirst().orElseThrow(() -> new NotFoundException("Route id not found"));

        RouteBase routeBase = (RouteBase) dbPersistentDao.retrieveDBPersistent(liteYukonPAObject);
        DBDeleteResult dbRes = new DBDeleteResult(routeId, DBDeletionDao.ROUTE_TYPE);
        DBDeleteResult dbDeleteRes = dbDeletionDao.getDeleteInfo(routeBase, liteYukonPAObject.getPaoName());
        byte statusCode = dbDeletionDao.deletionAttempted(dbRes);

        if (statusCode == DBDeletionDao.STATUS_DISALLOW) {
            throw new NotFoundException(
                    dbDeleteRes.getUnableDelMsg().toString() + " "
                            + dbRes.getDescriptionMsg().toString().replaceAll("\\r\\n", " "));
        }
        // TODO DBDeletionDao.STATUS_CONFIRM; In case of confirm we are allowing a user to delete a route because it is required
        // for UI
        dbPersistentDao.performDBChange(routeBase, TransactionType.DELETE);
        return routeBase.getPAObjectID();
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
