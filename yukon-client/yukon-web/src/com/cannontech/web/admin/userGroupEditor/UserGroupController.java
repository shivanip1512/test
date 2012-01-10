package com.cannontech.web.admin.userGroupEditor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserGroupController {
    
    /* User Group Editor Home Page */
    @RequestMapping("/userGroupEditor/home")
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/home.jsp";
    }
    
    @RequestMapping("/userGroupEditor/edit")
    public String edit(YukonUserContext userContext, ModelMap modelMap, Integer userId, Integer groupId, String typeToEdit) {
        if (typeToEdit.equals("group")) {
            modelMap.addAttribute("groupId", groupId);
            return "redirect:/spring/adminSetup/groupEditor/view";
        } else {
            modelMap.addAttribute("userId", userId);
            return "redirect:/spring/adminSetup/userEditor/view";
        }
    }

}