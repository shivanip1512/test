package com.cannontech.web.admin.userGroupEditor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.user.UserGroup;

public class UserGroupValidator extends SimpleValidator<UserGroup> {

    @Autowired private UserGroupDao userGroupDao;

    public UserGroupValidator() {
        super(UserGroup.class);
    }
    
    @Override
    public void doValidation(UserGroup userGroup, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "liteUserGroup.userGroupName", "required.userGroupName");
        YukonValidationUtils.checkExceedsMaxLength(errors, "liteUserGroup.userGroupName", userGroup.getLiteUserGroup().getUserGroupName(), 1000);
        
        LiteUserGroup possibleDuplicate = userGroupDao.getLiteUserGroupByUserGroupName(userGroup.getLiteUserGroup().getUserGroupName());
        if (possibleDuplicate != null && userGroup.getLiteUserGroup().getUserGroupId() != possibleDuplicate.getUserGroupId()) {
            errors.rejectValue("liteUserGroup.userGroupName", "unavailable.userGroupName");
        }
        
    }

}