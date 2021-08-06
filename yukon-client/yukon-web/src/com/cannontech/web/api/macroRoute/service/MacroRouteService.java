package com.cannontech.web.api.macroRoute.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;

public interface MacroRouteService {

    /*
     * Create a MacroRoute.
     */
    MacroRouteModel create(MacroRouteModel macroRouteModel, LiteYukonUser liteYukonUser);

    /*
     * Update a MacroRoute.
     */
    MacroRouteModel<?> update(int id, MacroRouteModel<?> macroRouteModel, LiteYukonUser yukonUser);

}
