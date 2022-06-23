package com.cannontech.web.api.notificationGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.notificationGroup.NotificationGroup;

public class NotificationGroupApiCreateValidator extends SimpleValidator<NotificationGroup> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public NotificationGroupApiCreateValidator() {
        super(NotificationGroup.class);
    }

    @Override
    protected void doValidation(NotificationGroup notificationGroup, Errors errors) {
        // Check if name is NULL
        if (!errors.hasFieldErrors("name")) {
            yukonApiValidationUtils.checkIfFieldRequired("name", errors, notificationGroup.getName(), "Name");
        }
    }
}