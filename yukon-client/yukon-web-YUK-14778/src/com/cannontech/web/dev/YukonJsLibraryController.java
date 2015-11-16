
package com.cannontech.web.dev;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.util.LazyList;
import com.cannontech.web.dev.model.Person;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableList;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class YukonJsLibraryController {
    
    public class Company {
        
        List<Person> employees = LazyList.ofInstance(Person.class);
        
        public List<Person> getEmployees() {
            return employees;
        }
        
        public void setEmployees(List<Person> employees) {
            this.employees = employees;
        }
    }
    
    @RequestMapping({"/js-api", "/js-api/"})
    public String root(ModelMap model) {
        
        model.addAttribute("favoriteNumber", 42);
        
        Company company = new Company();
        
        Person aaron = new Person();
        aaron.setName("Aaron");
        aaron.setEmail("aaron@eaton.com");
        company.getEmployees().add(aaron);
        
        Person sam = new Person();
        sam.setName("Sam");
        sam.setEmail("sam@eaton.com");
        company.getEmployees().add(sam);
        
        Person joe = new Person();
        joe.setName("Joe");
        joe.setEmail("joe@eaton.com");
        company.getEmployees().add(joe);
        
        model.addAttribute("company", company);
        
        List<String> restaurants = ImmutableList.of("Chipotle", "Pittsburgh Blue", "Latuff's");
        model.addAttribute("restaurants", restaurants);
        
        return "js-api/home.jsp";
    }
    
    @RequestMapping("/js-api/show-hide-ajax")
    public String showHideAjax() {
        return "js-api/ajax-content.jsp";
    }
    
    @RequestMapping("/js-api/dialog-focus")
    public String dialogFocus() {
        return "js-api/dialog-focus.jsp";
    }
    
}