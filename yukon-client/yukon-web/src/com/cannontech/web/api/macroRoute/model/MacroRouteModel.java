package com.cannontech.web.api.macroRoute.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.route.MacroRoute;

public class MacroRouteModel<T extends MacroRoute> extends DeviceBaseModel implements DBPersistentConverter<T> {

    private List<MacroRouteList> routeIds;

    public List<MacroRouteList> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(List<MacroRouteList> routeIds) {
        this.routeIds = routeIds;
    }

    @Override
    public void buildModel(MacroRoute macroRoute) {
        setName(macroRoute.getRouteName());
        setId(macroRoute.getRouteID());
        setType(macroRoute.getPaoType());
        setEnable(macroRoute.isDisabled());

        ArrayList<MacroRouteList> routeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(macroRoute.getMacroRouteVector())) {
            List<com.cannontech.database.db.route.MacroRoute> macroVector = macroRoute.getMacroRouteVector();
            for (com.cannontech.database.db.route.MacroRoute macroRoutes : macroVector) {
                MacroRouteList macroRouteList = new MacroRouteList();
                macroRouteList.buildModel(macroRoutes);
                routeIds.add(macroRouteList);
            }
            setRouteIds(routeIds);
        }
    }

    @Override
    public void buildDBPersistent(MacroRoute macroRoute) {

        if (getName() != null) {
            macroRoute.setRouteName(getName());
        }
        macroRoute.setDeviceID(0);
        macroRoute.setDefaultRoute("N");
        int i = 1;
        Vector<com.cannontech.database.db.route.MacroRoute> macroRouteVector = new Vector<>();
        if (!CollectionUtils.isEmpty(getRouteIds())) {
            for (MacroRouteList macroRouteList : getRouteIds()) {
                com.cannontech.database.db.route.MacroRoute macroRoutes = new com.cannontech.database.db.route.MacroRoute();
                macroRouteList.buildDBPersistent(macroRoutes);
                macroRoutes.setRouteOrder(i++);
                // For Update Case
                if (macroRoute.getRouteID() != null) {
                    macroRoutes.setRouteID(macroRoute.getRouteID());
                }
                macroRouteVector.add(macroRoutes);
            }
        }
        macroRoute.setMacroRouteVector(macroRouteVector);
    }
}
