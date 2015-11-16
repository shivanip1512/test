package com.cannontech.web.user.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.validator.ContactDtoValidator;
import com.cannontech.web.user.model.UserProfile;

@Component
public class UserProfileValidator extends SimpleValidator<UserProfile> {
    
    public static final String MSGKEY_EMAIL_REQUIRED = "yukon.web.modules.operator.accountGeneral.email.required";
    public static final String FIELDNAME_EMAIL = "email";
    public static final String PROFILE_CONTACT_NAME = "contact";

    @Autowired private ContactDtoValidator contactDtoValidator;

    public UserProfileValidator() {
        super(UserProfile.class);
    }

    @Override
    public void doValidation(UserProfile userProfile, Errors errors) {
        // TODO: When Username changes are re-enabled, add validations here.

        // Over-done as home/work phones are always null:
        errors.pushNestedPath(PROFILE_CONTACT_NAME);
        if (StringUtils.isEmpty(userProfile.getContact().getEmail())) {
            errors.rejectValue(FIELDNAME_EMAIL, MSGKEY_EMAIL_REQUIRED);
        }
        contactDtoValidator.doValidation(userProfile.getContact(), errors);
        errors.popNestedPath();
    }

}