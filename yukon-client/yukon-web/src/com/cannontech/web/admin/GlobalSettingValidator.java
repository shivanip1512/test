package com.cannontech.web.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.GlobalSettingTypeValidators;
import com.cannontech.web.admin.YukonConfigurationController.GlobalSettingsEditorBean;

public class GlobalSettingValidator extends SimpleValidator<GlobalSettingsEditorBean> {
    private static Map<GlobalSettingType, GlobalSettingTypeValidators.TypeValidator> validators =  new HashMap<>();

    // To add validation for a field, add the TypeValidator to validators map
    static {
        for (GlobalSettingType setting : GlobalSettingType.values()) {
            if (setting.getValidator() != null) {
                validators.put(setting, setting.getValidator());
            }
        }
    }

    public GlobalSettingValidator() {
        super(GlobalSettingsEditorBean.class);
    }

    @Override
    protected void doValidation(GlobalSettingsEditorBean globalSettingsBean, Errors errors) {

        Set<GlobalSettingType> globalSettingTypes = GlobalSettingType.getSettingsForCategory(globalSettingsBean.getCategory());
        Map<GlobalSettingType, Object> values = globalSettingsBean.getValues();

        for (GlobalSettingType globalSettingType : globalSettingTypes) {
            if (validators.containsKey(globalSettingType)) {
                validators.get(globalSettingType).validate(values.get(globalSettingType), errors, globalSettingType);
            }
        }
    }
}
