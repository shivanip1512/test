package com.cannontech.web.admin.userGroupEditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;

@Service
public class RoleGroupValidator extends SimpleValidator<LiteYukonGroup> {
    
    @Autowired private YukonGroupDao yukonGroupDao;
    
    private static final String key = "yukon.web.modules.adminSetup.auth.role.group.";
    
    public RoleGroupValidator() {
        super(LiteYukonGroup.class);
    }
    
    @Override
    protected void doValidation(LiteYukonGroup group, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", key + "groupNameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "groupName", group.getGroupName(), 120);
        
        LiteYukonGroup duplicate = yukonGroupDao.findLiteYukonGroupByName(group.getGroupName());
        if (duplicate != null && duplicate.getGroupID() != group.getGroupID()) {
            errors.rejectValue("groupName", key + "groupNameUnavailable");
        }
        
        YukonValidationUtils.checkExceedsMaxLength(errors, "groupDescription", group.getGroupDescription(), 200);
    }
    
}