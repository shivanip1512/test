package com.cannontech.web.tdc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TdcHomeController {

    @RequestMapping("/")
    public String redirect(ModelMap modelMap) {
        
        return "redirect:/tdc";
    }
    
    @RequestMapping("/tdc")
    public String home(ModelMap modelMap) {
        
        return "home.jsp";
    }
    
}