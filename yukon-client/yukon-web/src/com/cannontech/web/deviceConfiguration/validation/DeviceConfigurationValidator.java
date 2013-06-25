package com.cannontech.web.deviceConfiguration.validation;

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean.CategorySelection;

public class DeviceConfigurationValidator extends SimpleValidator<DeviceConfigurationBackingBean> {
    private final Set<CategoryType> supportedPaoTypes;
    
    private static final String baseKey = "yukon.web.modules.tools.configs.config";
    
    public DeviceConfigurationValidator(Set<CategoryType> supportedPaoTypes) {
        super(DeviceConfigurationBackingBean.class);
        this.supportedPaoTypes = supportedPaoTypes;
    }
    
    @Override
    protected void doValidation(DeviceConfigurationBackingBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "configName", baseKey + ".emptyName");
        
        List<CategorySelection> categoryIds = target.getCategorySelections();
        
        for (int index = 0; index < supportedPaoTypes.size(); index++) {
            if (index > categoryIds.size() || 
                categoryIds.get(index) == null || 
                categoryIds.get(index).getCategoryId() == null) {
                errors.rejectValue("categorySelections[" + index + "].categoryId", baseKey + ".noCategoryChosen");
            }
        }
    }
}
