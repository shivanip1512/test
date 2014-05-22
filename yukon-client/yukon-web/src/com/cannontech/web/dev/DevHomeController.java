package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class DevHomeController {

    @RequestMapping("/*")
    public String root() {
        return "development.jsp";
    }
    
}
