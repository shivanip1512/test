package com.cannontech.web.stars.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.service.RouteHelper;

public class RouteValidator<T extends RouteBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private YukonValidationHelper yukonValidationHelper;
    @Autowired  private RouteHelper routeHelper;

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
        String deviceId = routeBaseModel.getDeviceId() != null ? routeBaseModel.getDeviceId().toString() : null;
        
        yukonValidationHelper.checkIfFieldRequired("signalTransmitterId", errors, routeBaseModel.getSignalTransmitterId(),
                yukonValidationHelper.getMessage(key + "selectSignalTransmitter"));
        
        if(!errors.hasFieldErrors("signalTransmitterId")) {
            PaoType routePaoType = routeHelper.getRouteType(String.valueOf(routeBaseModel.getSignalTransmitterId()));
            yukonValidationHelper.validatePaoName(routeBaseModel.getDeviceName(), routePaoType, errors,
                    yukonValidationHelper.getMessage("yukon.common.name"), deviceId, "deviceName");
        }
        
    }
}