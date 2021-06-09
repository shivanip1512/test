package com.cannontech.web.api.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.route.model.RouteBaseModel;

@SuppressWarnings("rawtypes")
public class RouteApiValidator extends SimpleValidator<RouteBaseModel> {
    @Autowired private RouteApiValidatorHelper routeApiValidatorHelper;

    public RouteApiValidator() {
        super(RouteBaseModel.class);
    }

    @Override
    protected void doValidation(RouteBaseModel route, Errors errors) {

        String strRouteId = ServletUtils.getPathVariable("id");
        Integer id = strRouteId == null ? null : Integer.valueOf(strRouteId);

        if (route.getName() != null) {
            routeApiValidatorHelper.validateRouteName(errors, route.getName(), id);
        }

        if (route.getSignalTransmitterId() != null) {
            routeApiValidatorHelper.validateSignalTransmitterId(errors, route.getSignalTransmitterId());
        }
    }
}