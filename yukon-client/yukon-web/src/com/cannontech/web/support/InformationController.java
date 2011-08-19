package com.cannontech.web.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;

@Controller
public class InformationController {

    @RequestMapping("")
    public String main(ModelMap model) {

        model.addAttribute("versionDetails", VersionTools.getYukonDetails());
        model.addAttribute("systemInformation", CtiUtilities.getSystemInfoString());
        model.addAttribute("buildInfo", VersionTools.getBuildInfo());

        return "info.jsp";
    }

}
