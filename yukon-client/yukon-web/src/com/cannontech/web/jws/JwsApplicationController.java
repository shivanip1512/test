package com.cannontech.web.jws;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.JAVA_WEB_START_LAUNCHER_ENABLED)
public class JwsApplicationController {

    @RequestMapping("applications")
    public String applications(ModelMap model) {
        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());
        return "applications.jsp";
    }
}
