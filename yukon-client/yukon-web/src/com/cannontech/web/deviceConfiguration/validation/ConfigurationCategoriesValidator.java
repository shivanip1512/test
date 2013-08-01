package com.cannontech.web.deviceConfiguration.validation;

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.deviceConfiguration.model.ConfigurationCategoriesBackingBean;
import com.cannontech.web.deviceConfiguration.model.ConfigurationCategoriesBackingBean.CategorySelection;

public class ConfigurationCategoriesValidator extends SimpleValidator<ConfigurationCategoriesBackingBean> {
    private final Set<CategoryType> supportedPaoTypes;
    
    private static final String baseKey = "yukon.web.modules.tools.configs.config";
    
    public ConfigurationCategoriesValidator(Set<CategoryType> supportedPaoTypes) {
        super(ConfigurationCategoriesBackingBean.class);
        this.supportedPaoTypes = supportedPaoTypes;
    }
    
    @Override
    protected void doValidation(ConfigurationCategoriesBackingBean target, Errors errors) {
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
