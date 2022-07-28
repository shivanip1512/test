package com.cannontech.web.tools.notificationGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.yukon.IDatabaseCache;

public class NotificationGroupValidator extends SimpleValidator<NotificationGroup> {
    
    @Autowired private YukonValidationHelper yukonValidationHelper;
    @Autowired private IDatabaseCache databaseCache;
    private static final String key = "yukon.common";
    
    NotificationGroupValidator () {
        super(NotificationGroup.class);
    }

    @Override
    protected void doValidation(NotificationGroup notificationGroup, Errors errors) {
        String nameTxt = yukonValidationHelper.getMessage(key + ".name");
        yukonValidationHelper.checkIfFieldRequired("name", errors, notificationGroup.getName(), nameTxt);
        Integer id = notificationGroup.getId();
        
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", nameTxt, 40);
            databaseCache.getAllContactNotificationGroups().stream()
                    .filter(liteGroup -> liteGroup.getNotificationGroupName().equalsIgnoreCase(notificationGroup.getName().trim()))
                    .findAny()
                    .ifPresent(group -> {
                        if (id == null || group.getNotificationGroupID() != id) {
                            errors.rejectValue("name", "yukon.web.error.nameConflict", new Object[] { nameTxt }, "");
                        }
                    });
        }
    }
}