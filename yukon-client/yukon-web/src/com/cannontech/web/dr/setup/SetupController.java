package com.cannontech.web.dr.setup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/setup")
public class SetupController {

    @RequestMapping("/list")
    public String list() {
        return "dr/setup/list.jsp";
    }

}
