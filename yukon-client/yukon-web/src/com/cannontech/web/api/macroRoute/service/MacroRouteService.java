package com.cannontech.web.api.macroRoute.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;

public interface MacroRouteService {

    /*
     * Create a MacroRoute.
     */
    MacroRouteModel create(MacroRouteModel macroRouteModel, LiteYukonUser liteYukonUser);

    List<MacroRouteModel> retrieveAllMacroRoutes();

    MacroRouteModel<?> retrieve(int id);
    
    /*
     * Delete a MacroRoute.
     */
    int delete(int id, LiteYukonUser yukonUser);

    /*
     * Update a MacroRoute.
     */
    MacroRouteModel<?> update(int id, MacroRouteModel<?> macroRouteModel, LiteYukonUser yukonUser);

}
