package com.cannontech.web.api.macroRoute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.macroRoute.model.MacroRouteList;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;

@SuppressWarnings("rawtypes")
public class MacroRouteApiValidator<T extends MacroRouteModel<?>> extends SimpleValidator<T> {
    @Autowired private MacroRouteApiValidatorHelper macroRouteApiValidatorHelper;

    @SuppressWarnings("unchecked")
    public MacroRouteApiValidator() {
        super((Class<T>) MacroRouteModel.class);
    }

    public MacroRouteApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T macroRoute, Errors errors) {

        String strRouteId = ServletUtils.getPathVariable("id");
        Integer id = strRouteId == null ? null : Integer.valueOf(strRouteId);

        if (macroRoute.getName() != null) {
            macroRouteApiValidatorHelper.validateMacroRouteName(errors, macroRoute.getName(), id);
        }

        if (macroRoute.getRouteIds() != null) {
            for (MacroRouteList macroRouteList : macroRoute.getRouteIds()) {
                macroRouteApiValidatorHelper.validateRouteIds(errors, macroRouteList);
            }
        }
    }
}