package com.cannontech.web.api.macroRoute.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.route.MacroRoute;
import com.google.common.collect.Lists;

public class MacroRouteModel<T extends MacroRoute> extends DeviceBaseModel implements DBPersistentConverter<T> {

    private List<MacroRouteList> routeList = Lists.newArrayList();

    public List<MacroRouteList> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<MacroRouteList> routeList) {
        this.routeList  = routeList;
    }

    @Override
    public void buildModel(MacroRoute macroRoute) {
        setDeviceName(macroRoute.getRouteName());
        setDeviceId(macroRoute.getRouteID());
        setDeviceType(macroRoute.getPaoType());
        setEnable(macroRoute.isDisabled());

        ArrayList<MacroRouteList> routeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(macroRoute.getMacroRouteVector())) {
            List<com.cannontech.database.db.route.MacroRoute> macroVector = macroRoute.getMacroRouteVector();
            for (com.cannontech.database.db.route.MacroRoute macroRoutes : macroVector) {
                MacroRouteList macroRouteList = new MacroRouteList();
                macroRouteList.buildModel(macroRoutes);
                routeList.add(macroRouteList);
            }
            setRouteList(routeList);
        }
    }

    @Override
    public void buildDBPersistent(MacroRoute macroRoute) {

        if (getDeviceName() != null) {
            macroRoute.setRouteName(getDeviceName());
        }
        macroRoute.setDeviceID(0);
        macroRoute.setDefaultRoute("N");
        int i = 1;
        Vector<com.cannontech.database.db.route.MacroRoute> macroRouteVector = new Vector<>();
        if (!CollectionUtils.isEmpty(getRouteList())) {
            for (MacroRouteList macroRouteList : getRouteList()) {
                com.cannontech.database.db.route.MacroRoute macroRoutes = new com.cannontech.database.db.route.MacroRoute();
                macroRouteList.buildDBPersistent(macroRoutes);
                macroRoutes.setRouteOrder(i++);
                // For Update Case
                if (macroRoute.getRouteID() != null) {
                    macroRoutes.setRouteID(macroRoute.getRouteID());
                }
                macroRouteVector.add(macroRoutes);
            }
            macroRoute.setMacroRouteVector(macroRouteVector);
        }
    }
}
