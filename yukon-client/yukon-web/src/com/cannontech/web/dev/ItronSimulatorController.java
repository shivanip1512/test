package com.cannontech.web.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.simulator.model.AddProgramError;
import com.cannontech.dr.itron.simulator.model.ESIGroupError;
import com.cannontech.dr.itron.simulator.model.EditHANDeviceError;
import com.cannontech.dr.itron.simulator.model.ItronBasicError;
import com.cannontech.dr.itron.simulator.model.SimulatedItronSettings;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/itron/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class ItronSimulatorController {
    
    @Autowired private SimulatedItronSettings itronSimulatorSettings;
    @Autowired private ItronCommunicationService itronCommunicationService;
    
    @GetMapping("itronSimulator")
    public String itronSimulator(ModelMap model) {
        boolean simulatorRunning = false;
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
    public String test() {
        Hardware hardware = new Hardware();
        hardware.setMacAddress("12:13:14:15:16:17:18");
        AccountDto account = new AccountDto();
        account.setAccountNumber("123456789");
        try {
            itronCommunicationService.addDevice(hardware, account);
            LiteYukonPAObject group = new LiteYukonPAObject(1);
            group.setPaoName("itron load group test");
            itronCommunicationService.createGroup(group);
        } catch(ItronCommunicationException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:itronSimulator";
    }
    
    @PostMapping("saveSettings")
    public String saveSettings(@ModelAttribute("settings") SimulatedItronSettings settings) {
        itronSimulatorSettings = settings;
        return "redirect:itronSimulator";
    }
    
    @GetMapping("start")
    public String start() {
        //simulatorService.startSimulator();
        return "redirect:itronSimulator";
    }
    
    @GetMapping("stop")
    public String stop() {
        //simulatorService.stopSimulator();
        return "redirect:itronSimulator";
    }
}
