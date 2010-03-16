package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.validation.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.model.ChangeLoginBackingBean;

public class ChangeLoginValidator implements Validator {

    private AuthenticationService authenticationService;
    private RolePropertyDao rolePropertyDao;
    private YukonUserDao yukonUserDao;
    private LiteYukonUser residentialUser;
    private YukonUserContext userContext;
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return ChangeLoginBackingBean.class.isAssignableFrom(clazz); 
    }
    
    public ChangeLoginValidator(LiteYukonUser residentialUser, YukonUserContext userContext, AuthenticationService authenticationService, RolePropertyDao rolePropertyDao, YukonUserDao yukonUserDao){
        this.residentialUser = residentialUser;
        this.userContext = userContext;
        this.authenticationService  = authenticationService;
        this.rolePropertyDao = rolePropertyDao;
        this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void validate(Object target, Errors errors) {

        ChangeLoginBackingBean changeLoginBackingBean = (ChangeLoginBackingBean)target;

        // Username Validation
        boolean allowsUsernameChange = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, userContext.getYukonUser());
        if(!allowsUsernameChange) {
            errors.rejectValue("username", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.invalidUsernameChange");
        }
        
        ValidationUtils.rejectIfEmpty(errors, "username", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.usernameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "username", changeLoginBackingBean.getUsername(), 64);
        LiteYukonUser usernameCheckUser = yukonUserDao.getLiteYukonUser(changeLoginBackingBean.getUsername());
        if (usernameCheckUser != null &&
            (residentialUser.getUserID() != usernameCheckUser.getUserID() ||
             usernameCheckUser.getUserID() == UserUtils.USER_DEFAULT_ID)) {
            errors.rejectValue("username", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.usernameAlreadyExists");
        }
        
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "password1", changeLoginBackingBean.getPassword1(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "password2", changeLoginBackingBean.getPassword2(), 64);
        if (!StringUtils.isBlank(changeLoginBackingBean.getPassword1()) ||
            !StringUtils.isBlank(changeLoginBackingBean.getPassword2())) {
            
            boolean allowsPasswordChange = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, userContext.getYukonUser());
            boolean passwordSetSupported = authenticationService.supportsPasswordSet(residentialUser.getAuthType());
            if (!allowsPasswordChange || !passwordSetSupported) {
                errors.rejectValue("password1", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.invalidPasswordChange");
                errors.rejectValue("password2", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.invalidPasswordChange");
            }

            if (!changeLoginBackingBean.getPassword1().equals(changeLoginBackingBean.getPassword2())) {
                errors.rejectValue("password1", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.passwordNoMatch");
                errors.rejectValue("password2", "yukon.web.modules.operator.changeLogin.changeLoginBackingBean.passwordNoMatch");
            }
            
        }
    }

}
