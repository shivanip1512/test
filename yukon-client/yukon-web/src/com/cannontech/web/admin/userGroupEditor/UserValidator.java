package com.cannontech.web.admin.userGroupEditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.user.User;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public class UserValidator extends SimpleValidator<User> {
    
    @Autowired private YukonUserDao yukonUserDao;
    
    private final static String key = "yukon.web.modules.adminSetup.auth.user.";
    
    public UserValidator() {
        super(User.class);
    }
    
    @Override
    public void doValidation(User user, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", key + "required.username");
        YukonValidationUtils.checkExceedsMaxLength(errors, "username", user.getUsername(), 64);
        
        Integer userId = user.getUserId();
        
        LiteYukonUser possibleDuplicate = yukonUserDao.findUserByUsername(user.getUsername());
        if (possibleDuplicate != null) {
            
            if (userId == null || // New user being created.
                userId != possibleDuplicate.getUserID()) { // This user is not me. 
                errors.rejectValue("username", key + "unavailable.username");
            }
        }
    }
    
}