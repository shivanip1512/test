package com.cannontech.web.api.macroRoute.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;
import com.cannontech.web.api.macroRoute.service.MacroRouteService;

public class MacroRouteServiceImpl implements MacroRouteService {

    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public MacroRouteModel create(MacroRouteModel macroRouteModel, LiteYukonUser liteYukonUser) {
        MacroRoute macroRoute = new MacroRoute();
        macroRouteModel.buildDBPersistent(macroRoute);
        dbPersistentDao.performDBChange(macroRoute, TransactionType.INSERT);
        MacroRoute getMacroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(macroRoute);
        macroRouteModel.buildModel(getMacroRoute);
        return macroRouteModel;
    }

}
