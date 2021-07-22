package com.cannontech.web.api.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.tools.points.model.PointBaseModel;

@SuppressWarnings("rawtypes")
public class RouteApiValidator<T extends RouteBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private RouteApiValidatorHelper routeApiValidatorHelper;

    @SuppressWarnings("unchecked")
    public RouteApiValidator() {
        super((Class<T>) RouteBaseModel.class);
    }

    public RouteApiValidator(Class<T> objectType) {
        super(objectType);
    }
    
    @Override
    protected void doValidation(T route, Errors errors) {

        String strRouteId = ServletUtils.getPathVariable("id");
        Integer id = strRouteId == null ? null : Integer.valueOf(strRouteId);

        if (route.getName() != null) {
            routeApiValidatorHelper.validateRouteName(errors, route.getName(), id);
        }

        if (route.getSignalTransmitterId() != null) {
            routeApiValidatorHelper.validateSignalTransmitterId(errors, route.getSignalTransmitterId(), id);
        }
    }
}