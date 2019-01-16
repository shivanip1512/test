package com.cannontech.web.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronSimulatorService;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/itron/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class ItronSimulatorController {
    @Autowired ItronSimulatorService simulatorService;
    @Autowired ItronCommunicationService communicationService;
    

    @GetMapping("itronSimulator")
    public String itronSimulator() {
        return "itronSimulator.jsp";
    }
    
    @GetMapping("test")
    public String test() {
        simulatorService.startSimulator();
        communicationService.addHANDevice();
        return "redirect:itronSimulator";
    }
}
