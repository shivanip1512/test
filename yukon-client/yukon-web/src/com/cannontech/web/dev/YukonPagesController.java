
package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class YukonPagesController {
    
    @RequestMapping({"/pages", "/pages/"})
    public String root(ModelMap model) {
        
        return "pages.jsp";
    }
    
}