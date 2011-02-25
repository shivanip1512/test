package com.cannontech.web.admin.userGroupEditor.model;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.model.UpdatableYukonUser;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserValidator extends SimpleValidator<UpdatableYukonUser> {

    public YukonUserValidator() {
        super(UpdatableYukonUser.class);
    }

    private YukonUserDao yukonUserDao;
    
    @Override
    public void doValidation(UpdatableYukonUser user, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
        YukonValidationUtils.checkExceedsMaxLength(errors, "username", user.getUsername(), 64);
        LiteYukonUser possibleDuplicate = yukonUserDao.findUserByUsername(user.getUsername());
        if (possibleDuplicate != null && user.getUserID() != possibleDuplicate.getUserID()) {
            errors.rejectValue("username", "unavailable.username");
        }
        
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "user.password", user.getPassword(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "confirmPassword", user.getConfirmPassword(), 64);
        if (!StringUtils.isBlank(user.getPassword()) || !StringUtils.isBlank(user.getConfirmPassword())) {
            
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                errors.rejectValue("password", "password.mismatch");
                errors.rejectValue("confirmPassword", "password.mismatch");
            }
        }
    }

    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}