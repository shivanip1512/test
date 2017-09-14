package com.cannontech.web.smartNotifications;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class SmartNotificationSubscriptionValidator extends SimpleValidator<SmartNotificationSubscription> {

    private EmailValidator emailValidator = EmailValidator.getInstance();

    public SmartNotificationSubscriptionValidator() {
        super(SmartNotificationSubscription.class);
    }

    @Override
    public void doValidation(SmartNotificationSubscription subscription, Errors errors) {
        // email
        if (subscription.getMedia().equals(SmartNotificationMedia.EMAIL)) {
            String email = subscription.getRecipient();
            if (StringUtils.isEmpty(email)) {
                errors.rejectValue("recipient", "yukon.web.error.isBlank");
            } else {
                YukonValidationUtils.checkExceedsMaxLength(errors, "recipient", email, 100);
                if (!emailValidator.isValid(email)) {
                    errors.rejectValue("recipient", "yukon.web.modules.smartNotifications.email.invalid");
                }
            }
        }
    }

}
