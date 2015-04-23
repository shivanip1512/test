package com.cannontech.web.admin.userGroupEditor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/userEditor/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UsersAndGroupsHomeController {
    
    @Autowired private AuthenticationService authService;
    
    /** Users and Groups Home Page */
    @RequestMapping("home")
    public String home(ModelMap model) {
        
        AuthenticationCategory[] categories = AuthenticationCategory.values();
        Map<AuthenticationCategory, Boolean> passwordTypes = new HashMap<>();
        for (AuthenticationCategory category : categories) {
            passwordTypes.put(category, authService.supportsPasswordSet(category));
        }
        model.addAttribute("passwordTypes", passwordTypes);
        
        return "userGroupEditor/home.jsp";
    }
    
}