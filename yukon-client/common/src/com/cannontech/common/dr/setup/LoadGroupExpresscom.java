package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(value = { "routeName" }, allowGetters = true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class LoadGroupExpresscom extends LoadGroupRFNExpresscom implements LoadGroupRoute {
    private Integer routeId;
    private String routeName;

    @Override
    public Integer getRouteId() {
        return routeId;
    }

    @Override
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getRouteName() {
        return routeName;
    }

    @Override
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public void buildModel(LMGroupExpressCom lmGroupExpresscom) {
        // Set parent fields
        super.buildModel(lmGroupExpresscom);
        // Set expresscom fields
        setRouteId(lmGroupExpresscom.getLMGroupExpressComm().getRouteID());
    }

    @Override
    public void buildDBPersistent(LMGroupExpressCom lmGroupExpressCom) {
        // Set parent fields
        super.buildDBPersistent(lmGroupExpressCom);
        // Set Expresscom
        lmGroupExpressCom.getLMGroupExpressComm().setRouteID(getRouteId());
    }

}