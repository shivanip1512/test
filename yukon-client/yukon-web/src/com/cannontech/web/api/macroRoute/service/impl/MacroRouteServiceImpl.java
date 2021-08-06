package com.cannontech.web.api.macroRoute.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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
        macroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(macroRoute);
        macroRouteModel.buildModel(macroRoute);
        setRouteNameFromList(macroRouteModel);
        return macroRouteModel;
    }

    @Override
    public MacroRouteModel<?> retrieve(int id) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllRoutes()
                .stream()
                .filter(macroRoute -> macroRoute.getLiteID() == id && macroRoute.getPaoType().equals(PaoType.ROUTE_MACRO))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Macro Route Id not found"));
        MacroRoute macroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(pao);
        MacroRouteModel macroRouteModel = new MacroRouteModel();
        macroRouteModel.buildModel(macroRoute);
        setRouteNameFromList(macroRouteModel);
        return macroRouteModel;
    }

    @Override
    public List<MacroRouteModel> retrieveAllMacroRoutes() {
        List<LiteYukonPAObject> listOfMacroRoutes = serverDatabaseCache.getAllRoutes()
                .stream()
                .filter(macroRoute -> macroRoute.getPaoType().equals(PaoType.ROUTE_MACRO))
                .collect(Collectors.toList());
        List<MacroRouteModel> macroRouteModelList = new ArrayList();
        if (!CollectionUtils.isEmpty(listOfMacroRoutes)) {
            listOfMacroRoutes.forEach(yukonPAObject -> {
                MacroRoute macroRoute = (MacroRoute) dbPersistentDao.retrieveDBPersistent(yukonPAObject);
                MacroRouteModel macroRouteModel = new MacroRouteModel();
                macroRouteModel.buildModel(macroRoute);
                setRouteNameFromList(macroRouteModel);
                macroRouteModelList.add(macroRouteModel);
            });
        }
        return macroRouteModelList;
    }

    private void setRouteNameFromList(MacroRouteModel macroRouteModel) {
        List<MacroRouteList> routeList = macroRouteModel.getRouteList();
        for (MacroRouteList macroRouteList : routeList) {
            macroRouteList.setRouteName(serverDatabaseCache.getAllPaosMap().get(macroRouteList.getRouteId()).getPaoName());
        }
    }
}
