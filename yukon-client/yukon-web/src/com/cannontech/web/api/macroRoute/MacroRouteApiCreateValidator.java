package com.cannontech.web.api.macroRoute;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;

@SuppressWarnings("rawtypes")
public class MacroRouteApiCreateValidator extends SimpleValidator<MacroRouteModel> {
    public MacroRouteApiCreateValidator() {
        super(MacroRouteModel.class);
    }

    @Override
    protected void doValidation(MacroRouteModel macroRoute, Errors errors) {
        // Check if name is NULL
        YukonApiValidationUtils.checkIfFieldRequired("name", errors, macroRoute.getName(), "Name");
      
        // Check if RouteIds is NULL
        YukonApiValidationUtils.checkIfFieldRequired("routeIds", errors, macroRoute.getRouteIds(),
                "RouteIds");
    }
}