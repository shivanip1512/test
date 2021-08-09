package com.cannontech.web.api.macroRoute.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.web.api.macroRoute.model.MacroRouteList;
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
        macroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(macroRoute);
        macroRouteModel.buildModel(macroRoute);
        setRouteNameFromList(macroRouteModel);
        return macroRouteModel;
    }

    private void setRouteNameFromList(MacroRouteModel macroRouteModel) {
        List<MacroRouteList> routeList = macroRouteModel.getRouteList();
        for (MacroRouteList macroRouteList : routeList) {
            macroRouteList.setRouteName(serverDatabaseCache.getAllPaosMap().get(macroRouteList.getRouteId()).getPaoName());
        }
    }
}
