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
import com.cannontech.web.api.route.service.RouteHelper;
import com.cannontech.web.api.route.service.RouteService;
import com.cannontech.yukon.IDatabaseCache;

public class RouteServiceImpl implements RouteService {

    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DBDeletionDao dbDeletionDao;
    @Autowired private RouteHelper routeHelper;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    public RouteBaseModel<? extends RouteBase> create(RouteBaseModel routeBaseModel, LiteYukonUser yukonUser) {

        setRoutePaoType(routeBaseModel);
        RouteBase routeBase = RouteFactory.createRoute(routeBaseModel.getDeviceType());
        routeBaseModel.buildDBPersistent(routeBase);
        dbPersistentDao.performDBChange(routeBase, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(routeBase.getPAObjectID(), routeBase.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        routeBase = (RouteBase) dbPersistentDao.retrieveDBPersistent(routeBase);
        routeBaseModel.buildModel(routeBase);
        return routeBaseModel;

    }

    @Override
    public RouteBaseModel<? extends RouteBase> update(int id, RouteBaseModel routeBaseModel, LiteYukonUser liteYukonUser) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllRoutes().stream()
                .filter(route -> route.getLiteID() == id)
                .findFirst().orElseThrow(() -> new NotFoundException("Route id not found"));

        RouteBase routeBase = (RouteBase) dbPersistentDao.retrieveDBPersistent(pao);
        routeBaseModel.buildDBPersistent(routeBase);
        dbPersistentDao.performDBChange(routeBase, TransactionType.UPDATE);
        routeBaseModel.buildModel(routeBase);
        return routeBaseModel;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public RouteBaseModel<? extends RouteBase> retrieve(int routeId) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllRoutes().stream()
                .filter(route -> route.getLiteID() == routeId)
                .findFirst().orElseThrow(() -> new NotFoundException("Route id not found"));

        RouteBase routeBase = (RouteBase) dbPersistentDao.retrieveDBPersistent(pao);
        RouteBaseModel routeBaseModel = routeHelper.getRouteFromModelFactory(pao.getPaoType());// new factory
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
                RouteBaseModel routeBaseModel = routeHelper.getRouteFromModelFactory(yukonPAObject.getPaoType());
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
        PaoType routePaoType = routeHelper.getRouteType(String.valueOf(routeBaseModel.getSignalTransmitterId()));
        routeBaseModel.setDeviceType(routePaoType);
    }

}
