package com.cannontech.web.bulk;

import javax.servlet.jsp.JspException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/deviceSelection")
public class DeviceSelectionController {
    
    @RequestMapping
    public void display(ModelMap map, String errorMsg) throws JspException {
        
        map.addAttribute("errorMsg", errorMsg);
    }
}
