package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@RequestMapping("/uiToolkitDemo/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class UIToolkitDemo {
    
    @RequestMapping("main")
    public void main() { }
    
}