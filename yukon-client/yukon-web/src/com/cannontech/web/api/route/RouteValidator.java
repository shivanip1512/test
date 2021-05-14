package com.cannontech.web.api.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.route.model.RouteBaseModel;

@SuppressWarnings("rawtypes")
public class RouteValidator extends SimpleValidator<RouteBaseModel> {
    @Autowired private RouteApiValidatorHelper routeApiValidatorHelper;

    public RouteValidator() {
        super(RouteBaseModel.class);
    }

    @Override
    protected void doValidation(RouteBaseModel route, Errors errors) {

        if (route.getName() != null) {
            String strRouteId = ServletUtils.getPathVariable("id");
            Integer routeId = strRouteId == null ? null : Integer.valueOf(strRouteId);
            routeApiValidatorHelper.validateRouteName(errors, route.getName(), routeId);
        }

        if (route.getSignalTransmitterId() != null) {
            String strRouteId = ServletUtils.getPathVariable("id");
            Integer routeId = strRouteId == null ? null : Integer.valueOf(strRouteId);
            routeApiValidatorHelper.validateSignalTransmitterId(errors, route.getSignalTransmitterId(), routeId);
        }
    }
}