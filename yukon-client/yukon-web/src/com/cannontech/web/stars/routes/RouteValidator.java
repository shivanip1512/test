package com.cannontech.web.stars.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.web.api.route.model.RouteBaseModel;

public class RouteValidator<T extends RouteBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private YukonValidationHelper yukonValidationHelper;

    private static final String key = "yukon.web.modules.operator.routes.";

    @SuppressWarnings("unchecked")
    public RouteValidator() {
        super((Class<T>) RouteBaseModel.class);
    }

    public RouteValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T routeBaseModel, Errors errors) {
        String paoId = routeBaseModel.getDeviceId() != null ? routeBaseModel.getDeviceId().toString() : null;

        yukonValidationHelper.validatePaoName(routeBaseModel.getDeviceName(), routeBaseModel.getDeviceType(), errors,
                yukonValidationHelper.getMessage("yukon.common.name"), paoId, "deviceName");

        yukonValidationHelper.checkIfFieldRequired("signalTransmitterId", errors, routeBaseModel.getSignalTransmitterId(),
                yukonValidationHelper.getMessage(key + "selectSignalTransmitter"));
    }
}