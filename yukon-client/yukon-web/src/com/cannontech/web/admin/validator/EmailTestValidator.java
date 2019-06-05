package com.cannontech.web.admin.validator;

import static com.cannontech.web.stars.dr.operator.validator.ContactNotificationDtoValidator.MAX_NOTIFICATION_LENGTH;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.admin.config.model.AdminSetupEmailModel;

@Component
public class EmailTestValidator extends SimpleValidator<AdminSetupEmailModel> {
    public static final String MSGKEY_EMAIL_REQUIRED = "yukon.web.modules.operator.accountGeneral.email.required";
    public static final String FIELDNAME_EMAIL = "to";
    public static final String MSGKEY_EMAIL_INVALID = "yukon.web.modules.operator.accountGeneral.email.invalid";
    private EmailValidator emailValidator = EmailValidator.getInstance();

    public EmailTestValidator() {
        super(AdminSetupEmailModel.class);
    }
    
    @Override
    public void doValidation(AdminSetupEmailModel emailModel, Errors errors) {
        String email = emailModel.getTo();
        if (!StringUtils.isEmpty(email)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, FIELDNAME_EMAIL, email, MAX_NOTIFICATION_LENGTH);
            if (!emailValidator.isValid(email)) {
                errors.rejectValue(FIELDNAME_EMAIL, MSGKEY_EMAIL_INVALID);
            }
        } else {
            errors.rejectValue(FIELDNAME_EMAIL, MSGKEY_EMAIL_REQUIRED);
        }
    }
}
