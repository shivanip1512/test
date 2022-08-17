package com.cannontech.web.dev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.model.JobRunStatus;
import com.cannontech.util.Validator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.model.Person;
import com.cannontech.web.group.DisableNodeCallback;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class StyleGuideController {
    
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    
    private Map<Integer, Person> people = new ConcurrentHashMap<>(ImmutableMap.of(
            1, new Person(1, "Bob Vila", "bob@thisoldhouse.com", 50, true),
            2, new Person(2, "Paul Bunyan", "paul@mn.gov", 41, false),
            3, new Person(3, "Babe the Blue Ox", "babe@mn.gov", 8, true)));
    
    @RequestMapping({"/styleguide", "/styleguide/"})
    public String root() {
        return "redirect:styleguide/grids";
    }
    
    @RequestMapping("/styleguide/grids")
    public String grids() {
        return "styleguide/grids.jsp";
    }
    
    @RequestMapping("/styleguide/containers")
    public String containers() {
        return "styleguide/containers.jsp";
    }
    
    @RequestMapping("/styleguide/icons")
    public String icons(ModelMap model, HttpServletRequest request) {
        try {
            setupSprites(model, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "styleguide/icons.jsp";
    }
    
    @RequestMapping("/styleguide/labels-badges")
    public String labelsAndBadges() {
        return "styleguide/labels.and.badges.jsp";
    }
    
    @RequestMapping("/styleguide/progressbars")
    public String progressbars() {
        return "styleguide/progressbars.jsp";
    }
    
    @RequestMapping("/styleguide/timelines")
    public String timelines() {
        return "styleguide/timelines.jsp";
    }
    
    @RequestMapping("/styleguide/sliders")
    public String sliders() {
        return "styleguide/sliders.jsp";
    }
    
    @RequestMapping("/styleguide/steppers")
    public String steppers() {
        return "styleguide/steppers.jsp";
    }
    
    public class ButtonBean {
        private String phase = "B";
        
        public String getPhase() {
            return phase;
        }
        
        public void setPhase(String phase) {
            this.phase = phase;
        }
    }
    
    public class JobFilterBean {
        private JobRunStatus[] statuses = new JobRunStatus[] { JobRunStatus.FAILED, JobRunStatus.COMPLETED };
        
        public JobRunStatus[] getStatus() {
            return statuses;
        }
        
        public void setStatus(JobRunStatus[] statuses) {
            this.statuses = statuses;
        }
    }
    
    @RequestMapping("/styleguide/buttons")
    public String buttons(ModelMap model) {
        
        model.addAttribute("bean", new ButtonBean());
        model.addAttribute("jobfilter", new JobFilterBean());
        
        return "styleguide/buttons.jsp";
    }
    
    @RequestMapping("/styleguide/switches")
    public String switches(ModelMap model) {
        
        Thing thing = new Thing();
        thing.setEnabled(true);
        model.addAttribute("thing", thing);
        
        return "styleguide/switches.jsp";
    }
    
    @RequestMapping("/styleguide/alerts")
    public String alerts(ModelMap model) {
        
        model.addAttribute("errorMsg", "Something went wrong.");
        model.addAttribute("person", new Person());
        
        return "styleguide/alerts.jsp";
    }
    
    private static final String keyBase = "yukon.web.modules.dev.alerts.";
    
    @RequestMapping(value="/styleguide/alerts/flash-scope-test", method=RequestMethod.POST)
    public String alerts(FlashScope flash, @ModelAttribute("person") Person person, BindingResult result) {
        
        if (StringUtils.isBlank(person.getName())) {
            flash.setError(new YukonMessageSourceResolvable(keyBase + "error"));
        } else if (person.getEmail().contains("@aol.com")) {
            flash.setWarning(new YukonMessageSourceResolvable(keyBase + "warning"));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable(keyBase + "success"));
        }
        
        return "styleguide/alerts.jsp";
    }
    
    @RequestMapping("/styleguide/fun-with-inputs")
    public String inputs(ModelMap model) {
        model.addAttribute("numericInput", new NumericInput());
        return "styleguide/inputs.jsp";
    }
    
    public class NumericInput {
        private float temperature;

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }
    }
    
    @RequestMapping("/styleguide/blocking")
    public String blocking() {
        return "styleguide/blocking.jsp";
    }
    
    @RequestMapping("/styleguide/dialogs")
    public String dialogs(ModelMap model) {
        model.addAttribute("people", people.values());
        return "styleguide/dialogs.jsp";
    }
    
    @RequestMapping("/styleguide/dialogs/ajax-tabbed-dialog")
    public String ajaxTabbedDialog() {
        return "styleguide/ajax.tabbed.dialog.jsp";
    }
    
    @RequestMapping("/styleguide/pickers")
    public String pickers() {
        return "styleguide/pickers.jsp";
    }
    
    @RequestMapping("/styleguide/date-pickers")
    public String datePickers(ModelMap model) {
        
        Instant now = Instant.now();
        Instant weekAgo = new Instant(now.minus(Duration.standardDays(7)).getMillis());
        Instant weekFromNow = new Instant(now.plus(Duration.standardDays(7)).getMillis());
        model.addAttribute("now", now);
        model.addAttribute("weekAgo", weekAgo);
        model.addAttribute("weekFromNow", weekFromNow);
        
        return "styleguide/date.pickers.jsp";
    }
    
    @RequestMapping("/styleguide/group-pickers")
    public String groupPickers(ModelMap model) {
        
        List<String> groups = Lists.newArrayList("/Meters/Billing", "/Meters/Alternate", 
                "/Meters/Collection");
        model.addAttribute("groups", groups);
        model.addAttribute("group", Lists.newArrayList("/Meters/Billing"));
        
        DeviceGroup flags = deviceGroupService.findGroupName("/Meters/Flags");
        DisableNodeCallback noFlags = DisableNodeCallback.of(flags);
        model.addAttribute("noFlags", ImmutableSet.of(noFlags));
        
        return "styleguide/group.pickers.jsp";
    }
    
    @RequestMapping("/styleguide/device-collections")
    public String deviceCollections(ModelMap model) {
        
        DeviceGroup meterGroup = deviceGroupService.findGroupName("/Meters");
        Set<Integer> deviceIds = deviceGroupService.getDeviceIds(ImmutableList.of(meterGroup));
        DeviceCollection dc = dcProducer.createDeviceCollection(new ArrayList<>(deviceIds), null);
        
        model.addAttribute("dc", dc);
        
        return "styleguide/device.collections.jsp";
    }
    
    private void setupSprites(ModelMap model, HttpServletRequest request) throws IOException {
        ArrayList<String> sprites16Array = new ArrayList<String>();
        ArrayList<String> sprites32Array = new ArrayList<String>();
        ArrayList<String> spritesNew32Array = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(request.getServletContext().getRealPath("/WebConfig/yukon/styles/icons.css")));
        try {
            String line = br.readLine();
            while (line != null) {
                if (line.contains("icon-32-")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        sprites32Array.add(line.substring(1, endIndex));
                    }
                } else if (line.contains("icon-") && !line.contains("icon-32") && !line.contains("icon-app")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        sprites16Array.add(line.substring(1, endIndex));
                    }
                } else if (line.contains("icon-app-32-")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        spritesNew32Array.add(line.substring(1, endIndex));
                    }
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        model.addAttribute("sprites16Array", sprites16Array);
        model.addAttribute("sprites32Array", sprites32Array);
        model.addAttribute("spritesNew32Array", spritesNew32Array);
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
        model.addAttribute("create", true);
        
        return "styleguide/person.jsp";
    }
    
    @RequestMapping(value="/styleguide/person", method=RequestMethod.POST)
    public String createPerson(ModelMap model, HttpServletResponse resp, 
            @ModelAttribute("person") Person person, BindingResult result) {
        
        new PersonValidator().validate(person, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("create", true);
            return "styleguide/person.jsp";
        }
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        return null;
    }
    
    @RequestMapping(value="/styleguide/edit-person", method=RequestMethod.GET)
    public String editPersonPopup(ModelMap model, int id) {
        
        Person person = people.get(id);
        model.addAttribute("person", person);
        
        return "styleguide/person.jsp";
    }
    
    @RequestMapping(value="/styleguide/person", method=RequestMethod.PUT)
    public String updatePerson(ModelMap model, HttpServletResponse resp, 
            @ModelAttribute("person") Person person, BindingResult result) throws Exception {
        
        new PersonValidator().validate(person, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "styleguide/person.jsp";
        }
        
        people.put(person.getId(), person);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), person);
        
        return null;
    }
    
}