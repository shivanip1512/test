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
@RequestMapping("/pxWhite/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PxWhiteSimulatorController {

	@GetMapping("/home")
	public String home(ModelMap model) {
	    model.addAttribute("endpoints", RetrievalUrl.values());
	    SimulatedPxWhiteSettings settings = new SimulatedPxWhiteSettings();
	    for (RetrievalUrl url : RetrievalUrl.values()) {
	        settings.getSelectedStatuses().put(url, url.getStatuses().get(0));
	    }
	    model.addAttribute("settings", settings);
		return "pxWhite/home.jsp";
	}
	
   @PostMapping("/updateSettings")
    public String updateSettings(@ModelAttribute("settings") SimulatedPxWhiteSettings settings) {
       
       return "redirect:home";
    }

}


