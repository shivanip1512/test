package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/pxMW/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PxMWSimulatorController {

	@GetMapping("/home")
	public String home(ModelMap model) {
	    model.addAttribute("endpoints", RetrievalUrl.values());
	    model.addAttribute("settings", new SimulatedPxWhiteSettings());
		return "pxMW/home.jsp";
	}
	
   @PostMapping("/updateSettings")
    public String updateSettings(@ModelAttribute("settings") SimulatedPxWhiteSettings settings) {
       
       return "redirect:home";
    }
   
   @PostMapping("/testEndpoint")
   public String testEndpoint(RetrievalUrl endpoint) {
      
      return "redirect:home";
   }

}


