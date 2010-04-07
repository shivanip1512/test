package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.model.ChangeLoginBackingBean;

public class ChangeLoginValidator extends SimpleValidator<ChangeLoginBackingBean> {

    private YukonUserDao yukonUserDao;
    private LiteYukonUser residentialUser;
    
    public ChangeLoginValidator(LiteYukonUser residentialUser, YukonUserDao yukonUserDao){
    	super(ChangeLoginBackingBean.class);
    	this.residentialUser = residentialUser;
        this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void doValidation(ChangeLoginBackingBean target, Errors errors) {

        ChangeLoginBackingBean changeLoginBackingBean = (ChangeLoginBackingBean)target;

        ValidationUtils.rejectIfEmpty(errors, "username", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.usernameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "username", changeLoginBackingBean.getUsername(), 64);
        LiteYukonUser usernameCheckUser = yukonUserDao.getLiteYukonUser(changeLoginBackingBean.getUsername());
        if (usernameCheckUser != null &&
            residentialUser.getUserID() != usernameCheckUser.getUserID()) {
            errors.rejectValue("username", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.usernameAlreadyExists");
        }
        
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "password1", changeLoginBackingBean.getPassword1(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "password2", changeLoginBackingBean.getPassword2(), 64);
        if (!StringUtils.isBlank(changeLoginBackingBean.getPassword1()) ||
            !StringUtils.isBlank(changeLoginBackingBean.getPassword2())) {
            
            if (!changeLoginBackingBean.getPassword1().equals(changeLoginBackingBean.getPassword2())) {
                errors.rejectValue("password1", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.passwordNoMatch");
                errors.rejectValue("password2", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.passwordNoMatch");
            }
        }
    }

}
