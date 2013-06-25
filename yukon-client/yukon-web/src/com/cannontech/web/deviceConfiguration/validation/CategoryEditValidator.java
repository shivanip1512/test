package com.cannontech.web.deviceConfiguration.validation;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.Field;
import com.cannontech.web.deviceConfiguration.model.FloatField;
import com.cannontech.web.deviceConfiguration.model.IntegerField;
import com.cannontech.web.input.validate.InputValidator;

public class CategoryEditValidator extends SimpleValidator<CategoryEditBean> {
    private static final Logger log = YukonLogManager.getLogger(CategoryEditValidator.class);
    
    private final CategoryTemplate categoryTemplate;
    
    private static final String baseKey = "yukon.web.modules.tools.configs.category";
    
    public CategoryEditValidator(CategoryTemplate categoryTemplate) {
        super(CategoryEditBean.class);
        this.categoryTemplate = categoryTemplate;
    }
    
    @Override
    protected void doValidation(CategoryEditBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryName", baseKey + ".emptyName");
    
        List<Field<?>> fields = categoryTemplate.getFields();
        
        for (Field<?> field : fields) {
            if (field.getInputType() != null) {
                String value = target.getCategoryInputs().get(field.getFieldName());
    
                final String path = "categoryInputs[" + field.getFieldName() + "]";
                
                if (StringUtils.isBlank(value)) {
                    errors.rejectValue(path, baseKey + ".emptyValue");
                    continue;
                }
                
                if (field.getValidator() != null) {
                    try {
                        if (field.getClass() == IntegerField.class) {
                            IntegerField intField = (IntegerField) field;
                            InputValidator<Integer> validator = intField.getValidator();
                            validator.validate(path, field.getDisplayName(), Integer.valueOf(value), errors);
                        } else if (field.getClass() == FloatField.class) {
                            FloatField floatField = (FloatField) field;
                            InputValidator<Float> validator = floatField.getValidator();
                            validator.validate(path, field.getDisplayName(), Float.valueOf(value), errors);
                        } else {
                            log.error("Received a validator for an unsupoorted type: " + field.getClass().getSimpleName());
                        }
                    } catch (NumberFormatException nfe) {
                        // The value wasn't a number
                        errors.rejectValue(path, baseKey + ".nonNumber");
                    }
                }
            }
            // TODO: Else case is a map. There should be some thought put into how validating of the maps will look.
        }
    }
}
