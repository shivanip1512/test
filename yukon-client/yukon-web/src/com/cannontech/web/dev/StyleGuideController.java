package com.cannontech.web.dev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class StyleGuideController {
    
    @RequestMapping({"/styleguide", "/styleguide/"})
    public String root() {
        return "redirect:styleguide/grids";
    }
    
    @RequestMapping("/styleguide/grids")
    public String grids() {
        return "styleguide/grids.jsp";
    }
    
    @RequestMapping("/styleguide/containers")
    public String containers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/containers.jsp";
    }
    
    @RequestMapping("/styleguide/buttons")
    public String buttons(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/buttons.jsp";
    }
    
    @RequestMapping("/styleguide/switches")
    public String switches(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        
        Thing thing = new Thing();
        thing.setEnabled(true);
        model.addAttribute("thing", thing);
        
        return "styleguide/switches.jsp";
    }
    
    @RequestMapping("/styleguide/fun-with-inputs")
    public String inputs(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/inputs.jsp";
    }
    
    @RequestMapping("/styleguide/blocking")
    public String blocking(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/blocking.jsp";
    }
    
    @RequestMapping("/styleguide/pickers")
    public String pickers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/pickers.jsp";
    }
    
    @RequestMapping("/styleguide/dialogs")
    public String dialogs(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/dialogs.jsp";
    }
    
    @RequestMapping("/styleguide/icons")
    public String icons(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        try {
            setupSprites(model, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "styleguide/icons.jsp";
    }
    
    @RequestMapping("/styleguide/date-pickers")
    public String more(ModelMap model) {
        
        Instant now = Instant.now();
        Instant weekAgo = new Instant(now.minus(Duration.standardDays(7)).getMillis());
        Instant weekFromNow = new Instant(now.plus(Duration.standardDays(7)).getMillis());
        model.addAttribute("now", now);
        model.addAttribute("weekAgo", weekAgo);
        model.addAttribute("weekFromNow", weekFromNow);
        
        return "styleguide/date.pickers.jsp";
    }
    
    private void setupSprites(ModelMap model, HttpServletRequest request) throws IOException {
        ArrayList<String> sprites16Array = new ArrayList<String>();
        ArrayList<String> sprites32Array = new ArrayList<String>();
        
        BufferedReader br = new BufferedReader(new FileReader(request.getServletContext().getRealPath("/WebConfig/yukon/styles/icons.css")));
        try {
            String line = br.readLine();
            while (line != null) {
                if (line.contains("icon-32-")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        sprites32Array.add(line.substring(1, endIndex));
                    }
                } else if (line.contains("icon-") && !line.contains("icon-32")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        sprites16Array.add(line.substring(1, endIndex));
                    }
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        model.addAttribute("sprites16Array", sprites16Array);
        model.addAttribute("sprites32Array", sprites32Array);
    }
    
    public class Thing {
        private boolean enabled;
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}