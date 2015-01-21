package com.cannontech.web.dev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.Validator;
import com.cannontech.web.dev.model.Person;
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
    
    public class PersonValidator extends SimpleValidator<Person> {
        
        public PersonValidator() { super(Person.class); }
        
        @Override
        protected void doValidation(Person person, Errors errors) {
            YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "name", person.getName(), false, 60);
            int age = person.getAge();
            String email = person.getEmail();
            if (age < 1 || age > 120) errors.rejectValue("age", null, "Age should be between 1 and 120.");
            if (!Validator.isEmailAddress(email)) errors.rejectValue("email", null, "Invalid email address.");
        }
    }
    
    @RequestMapping(value="/styleguide/new-person", method=RequestMethod.GET)
    public String newPersonPopup(ModelMap model) {
        
        Person person = new Person();
        model.addAttribute("person", person);
        
        return "styleguide/person.jsp";
    }
    
    @RequestMapping(value="/styleguide/person", method=RequestMethod.POST)
    public String createPerson(ModelMap model, HttpServletResponse resp, 
            @ModelAttribute("person") Person person, BindingResult result) {
        
        new PersonValidator().validate(person, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "styleguide/person.jsp";
        }
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        return null;
    }
    
}