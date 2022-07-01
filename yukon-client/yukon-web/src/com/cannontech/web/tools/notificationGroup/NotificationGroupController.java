package com.cannontech.web.tools.notificationGroup;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;

@Controller
@RequestMapping("/notificationGroup/*")
public class NotificationGroupController {
    
    @GetMapping("create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        
        return "/notificationGroup/view.jsp";
    }
    
    @GetMapping("list")
    public String list() {
        return "/notificationGroup/list.jsp";
    }
}
