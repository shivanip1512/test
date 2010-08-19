package com.cannontech.common.device.config.model;

import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.web.input.validate.InputValidator;

public class MCT420DisplayConfigValidator implements InputValidator<MCT420Configuration> {

    @Override
    public String getDescription() {
        return "Ensures all display items are contiguous and at the beginning of the list";
    }

    @Override
    public void validate(String path, String displayName,
                         MCT420Configuration value, Errors errors) {
        
        List<Integer> displayItems = value.getDisplayItems();
        boolean foundDisabled = false;
        for (int displayItemValue : displayItems) {
            if (foundDisabled && displayItemValue != 0) {
                errors.reject("i18n code here", "All enabled slots must be contiguous and at the beginning of the list.");
                return;
            }
            if (displayItemValue == 0) {
                foundDisabled = true;
            }
        }
    }

}
