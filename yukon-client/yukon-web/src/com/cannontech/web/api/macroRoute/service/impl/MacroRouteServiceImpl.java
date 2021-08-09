package com.cannontech.web.api.macroRoute.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
    

    @Override
    public MacroRouteModel<?> update(int id, MacroRouteModel<?> macroRouteModel, LiteYukonUser yukonUser) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllRoutes()
                .stream()
                .filter(group -> group.getLiteID() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Macro Route Id not found"));
        if (pao == null) {
            throw new NotFoundException("Route Id not found");
        }
        MacroRoute macroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(pao);
        macroRouteModel.buildDBPersistent(macroRoute);
        dbPersistentDao.performDBChange(macroRoute, TransactionType.UPDATE);
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
