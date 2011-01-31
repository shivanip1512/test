package com.cannontech.web.admin.userGroupEditor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RolePropertyController {

    @RequestMapping(value="/groupEditor/roleProperties")
    public String view(ModelMap modelMap) {
        /* TODO */
        return "userGroupEditor/roles.jsp";
    }
    
}