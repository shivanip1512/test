package com.cannontech.web.api.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.api.route.model.RouteBaseModel;

@SuppressWarnings("rawtypes")
public class RouteApiCreateValidator extends SimpleValidator<RouteBaseModel> {
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public RouteApiCreateValidator() {
        super(RouteBaseModel.class);
    }

    @Override
    protected void doValidation(RouteBaseModel route, Errors errors) {
        // Check if name is NULL
        yukonApiValidationUtils.checkIfFieldRequired("deviceName", errors, route.getDeviceName(), "Name");
        // Check if signalTransmitterID is NULL
        yukonApiValidationUtils.checkIfFieldRequired("signalTransmitterId", errors, route.getSignalTransmitterId(),
                "SignalTransmitterId");
    }
}