package com.cannontech.web.admin.userGroupEditor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserValidator extends SimpleValidator<LiteYukonUser> {

    public YukonUserValidator() {
        super(LiteYukonUser.class);
    }

    private YukonUserDao yukonUserDao;
    
    @Override
    public void doValidation(LiteYukonUser user, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
        YukonValidationUtils.checkExceedsMaxLength(errors, "username", user.getUsername(), 64);
        LiteYukonUser possibleDuplicate = yukonUserDao.findUserByUsername(user.getUsername());
        if (possibleDuplicate != null && user.getUserID() != possibleDuplicate.getUserID()) {
            errors.rejectValue("username", "unavailable.username");
        }
        
    }

    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}