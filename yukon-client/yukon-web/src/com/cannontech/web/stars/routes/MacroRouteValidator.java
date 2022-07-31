package com.cannontech.web.stars.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;

public class MacroRouteValidator extends SimpleValidator<MacroRouteModel> {

    @Autowired private YukonValidationHelper yukonValidationHelper;

    private static final String key = "yukon.web.modules.operator.routes.";

    @SuppressWarnings("unchecked")
    MacroRouteValidator() {
        super(MacroRouteModel.class);
    }

    @Override
    protected void doValidation(MacroRouteModel macroRouteModel, Errors errors) {
        String nameTxt = yukonValidationHelper.getMessage("yukon.common.name");
        yukonValidationHelper.validatePaoName(macroRouteModel.getDeviceName(), PaoType.ROUTE_MACRO, errors, nameTxt,
                macroRouteModel.getDeviceId() == null? null : String.valueOf(macroRouteModel.getDeviceId()), "deviceName");
    }

}
