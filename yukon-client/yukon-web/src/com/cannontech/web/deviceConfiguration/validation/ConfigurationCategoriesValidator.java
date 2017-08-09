package com.cannontech.web.deviceConfiguration.validation;

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.deviceConfiguration.model.ConfigCategories;
import com.cannontech.web.deviceConfiguration.model.ConfigCategories.CategorySelection;

public class ConfigurationCategoriesValidator extends SimpleValidator<ConfigCategories> {
    private final Set<CategoryType> categoryTypes;
    
    private static final String baseKey = "yukon.web.modules.tools.configs.config";
    
    public ConfigurationCategoriesValidator(Set<CategoryType> includedCategories) {
        super(ConfigCategories.class);
        this.categoryTypes = includedCategories;
    }
    
    @Override
    protected void doValidation(ConfigCategories target, Errors errors) {
        List<CategorySelection> categoryIds = target.getCategorySelections();
        
        for (int index = 0; index < categoryTypes.size(); index++) {
            if (index > categoryIds.size() || 
                categoryIds.get(index) == null || 
                categoryIds.get(index).getCategoryId() == null) {
                errors.rejectValue("categorySelections[" + index + "].categoryId", baseKey + ".noCategoryChosen");
            }
        }
    }
}
