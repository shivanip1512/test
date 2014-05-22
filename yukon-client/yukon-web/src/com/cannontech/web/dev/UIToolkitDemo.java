package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@RequestMapping("/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class UIToolkitDemo {
    
    @RequestMapping("uiToolkitDemo/main")
    public void main() { }
    
    @RequestMapping("main")
    public String developmentPage(){
        return "development.jsp";
    }
    
}