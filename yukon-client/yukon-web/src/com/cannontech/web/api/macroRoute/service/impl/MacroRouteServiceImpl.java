package com.cannontech.web.api.macroRoute.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;
import com.cannontech.web.api.macroRoute.service.MacroRouteService;
import com.cannontech.yukon.IDatabaseCache;

public class MacroRouteServiceImpl implements MacroRouteService {

    @Autowired private DBPersistentDao dbPersistentDao;
    
    @Autowired private IDatabaseCache serverDatabaseCache;

    @Override
    public MacroRouteModel create(MacroRouteModel macroRouteModel, LiteYukonUser liteYukonUser) {
        MacroRoute macroRoute = new MacroRoute();
        macroRouteModel.buildDBPersistent(macroRoute);
        dbPersistentDao.performDBChange(macroRoute, TransactionType.INSERT);
        MacroRoute getMacroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(macroRoute);
        macroRouteModel.buildModel(getMacroRoute);
        return macroRouteModel;
    }
    
    @Override
    public int delete(int id, LiteYukonUser yukonUser) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllRoutes()
                .stream()
                .filter(macroRoute -> macroRoute.getLiteID() == id && macroRoute.getPaoType().equals(PaoType.ROUTE_MACRO))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Macro Route Id not found"));
        MacroRoute macroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(pao);
        dbPersistentDao.performDBChange(macroRoute, TransactionType.DELETE);
        return macroRoute.getPAObjectID();
    }

}
