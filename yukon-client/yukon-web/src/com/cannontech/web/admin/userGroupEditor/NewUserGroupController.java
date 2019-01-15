package com.cannontech.web.admin.userGroupEditor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.db.user.UserGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class NewUserGroupController {
    
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private UsersEventLogService userEventLogService;
    
    private static final String key = "yukon.web.modules.adminSetup.auth.user.group.";
    
    private final Validator validator = new SimpleValidator<UserGroup>(UserGroup.class) {
        @Override
        public void doValidation(UserGroup userGroup, Errors errors) {
            
            String name = userGroup.getUserGroupName();
            Integer groupId = userGroup.getUserGroupId();
            
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "userGroupName", key + "name.required");
            YukonValidationUtils.checkExceedsMaxLength(errors, "userGroupName", name, 1000);
            
            LiteUserGroup possibleDuplicate = userGroupDao.findLiteUserGroupByUserGroupName(name);
            if (possibleDuplicate != null) {
                if (groupId == null // Creating new group. 
                        || groupId != possibleDuplicate.getUserGroupId()) { // This group is not me.
                    errors.rejectValue("userGroupName", key + "name.unavailable");
                }
            }
        }
    };
    
    @RequestMapping("new-user-group-dialog")
    public String newGroup(ModelMap model) {
        
        UserGroup group = new UserGroup();
        model.addAttribute("group", group);
        
        return "userGroupEditor/new-user-group.jsp";
    }
    
    @RequestMapping(value="user-groups", method=RequestMethod.POST)
    public String create(ModelMap model, HttpServletRequest req, HttpServletResponse resp, 
            @ModelAttribute("group") UserGroup group, BindingResult binding,
            YukonUserContext userContext) throws Exception {

        validator.validate(group, binding);
        
        if (binding.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "userGroupEditor/new-user-group.jsp";
        }
        
        userGroupDao.create(group);
        userEventLogService.userGroupCreated(group.getUserGroupName(), userContext.getYukonUser());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("userGroupId", group.getUserGroupId());
        
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), result);
        
        return null;
    }
    
}