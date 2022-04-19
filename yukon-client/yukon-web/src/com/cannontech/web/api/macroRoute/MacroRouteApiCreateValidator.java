package com.cannontech.web.api.macroRoute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;

@SuppressWarnings("rawtypes")
public class MacroRouteApiCreateValidator extends SimpleValidator<MacroRouteModel> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public MacroRouteApiCreateValidator() {
        super(MacroRouteModel.class);
    }

    @Override
    protected void doValidation(MacroRouteModel macroRoute, Errors errors) {
        // Check if name is NULL
        yukonApiValidationUtils.checkIfFieldRequired("deviceName", errors, macroRoute.getDeviceName(), "Name");
      
        // Check if RouteIds is NULL
        yukonApiValidationUtils.checkIfFieldRequired("routeList", errors, macroRoute.getRouteList(),
                "RouteIds");
    }
}