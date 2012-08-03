package com.cannontech.web.admin.userGroupEditor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/userEditor/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserGroupController {
    
    /* User Group Editor Home Page */
    @RequestMapping
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/home.jsp";
    }
}