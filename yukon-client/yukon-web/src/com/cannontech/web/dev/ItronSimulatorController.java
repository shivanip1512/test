package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/itron/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class ItronSimulatorController {
  
    @GetMapping("itronSimulator")
    public String itronSimulator() {
        return "itronSimulator.jsp";
    }
    
    @GetMapping("test")
    public String clearAllEvents() {
        return "redirect:itronSimulator";
    }

}
