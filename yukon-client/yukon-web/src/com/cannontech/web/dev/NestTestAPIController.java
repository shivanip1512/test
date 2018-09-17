package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/nestApi/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NestTestAPIController {

    @RequestMapping(value = "/v1/users/current/latest.csv")
    public void existing() {
        // TODO - code for existing file download
        System.out.println("Download file");
    }
    
}
