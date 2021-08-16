package com.cannontech.web.api.macroRoute.model;

import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MacroRouteList<T extends com.cannontech.database.db.route.MacroRoute> implements DBPersistentConverter<T> {

    @Autowired private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);;
    
    private Integer routeId;
    private String routeName;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public void buildModel(com.cannontech.database.db.route.MacroRoute macroRoutes) {
        setRouteId(macroRoutes.getSingleRouteID());
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.route.MacroRoute macroRoutes) {
        if (getRouteId() != null) {
            macroRoutes.setSingleRouteID(getRouteId());
        }
    }

}
