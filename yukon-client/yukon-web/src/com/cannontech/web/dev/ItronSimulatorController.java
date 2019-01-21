package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.itron.simulator.model.AddProgramError;
import com.cannontech.dr.itron.simulator.model.ESIGroupError;
import com.cannontech.dr.itron.simulator.model.EditHANDeviceError;
import com.cannontech.dr.itron.simulator.model.ItronBasicError;
import com.cannontech.dr.itron.simulator.model.SimulatedItronSettings;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/itron/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class ItronSimulatorController {
  
    @GetMapping("itronSimulator")
    public String itronSimulator(ModelMap model) {
        boolean simulatorRunning = simulatorService.isSimulatorRunning();
        model.addAttribute("simulatorRunning", simulatorRunning);
        SimulatedItronSettings settings = itronSimulatorSettings;
        model.addAttribute("settings", settings);
        model.addAttribute("editHANDeviceErrorTypes", EditHANDeviceError.values());
        model.addAttribute("basicErrorTypes", ItronBasicError.values());
        model.addAttribute("addProgramErrorTypes", AddProgramError.values());
        model.addAttribute("esiGroupErrorTypes", ESIGroupError.values());
        return "itronSimulator.jsp";
    }
    
    @GetMapping("test")
    public String clearAllEvents() {
        return "redirect:itronSimulator";
    }

}
