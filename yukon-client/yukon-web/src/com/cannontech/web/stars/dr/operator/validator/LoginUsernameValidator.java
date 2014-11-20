package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.login.model.Login;

public class LoginUsernameValidator extends SimpleValidator<Login> {

    private YukonUserDao yukonUserDao;

    private LiteYukonUser user;
    
    public LoginUsernameValidator(LiteYukonUser user, YukonUserDao yukonUserDao){
    	super(Login.class);
    	this.user = user;
    	this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void doValidation(Login loginBackingBean, Errors errors) {
    	//check username not empty
    	ValidationUtils.rejectIfEmpty(errors, "username", "yukon.web.modules.operator.account.loginInfoError.usernameRequired");
        if(loginBackingBean.getUsername().trim().length() != loginBackingBean.getUsername().length()){
        	errors.rejectValue("username", "yukon.web.modules.operator.account.loginInfoError.invalidUsername");
        }
        
        //check username not too long
        YukonValidationUtils.checkExceedsMaxLength(errors, "username", loginBackingBean.getUsername(), 64);
        
        //check username already used by a different user
        LiteYukonUser usernameAlreadyUsedUser = yukonUserDao.findUserByUsername(loginBackingBean.getUsername());
        if (usernameAlreadyUsedUser != null) {
        	if (user == null) {	//new user 
        		errors.rejectValue("username", "yukon.web.modules.operator.account.loginInfoError.usernameAlreadyExists");
        		return;
        	} else if (user.getUserID() != usernameAlreadyUsedUser.getUserID()) {	//existing user; check if not this.user
                errors.rejectValue("username", "yukon.web.modules.operator.account.loginInfoError.usernameAlreadyExists");
                return;
        	}
        }
    }
}