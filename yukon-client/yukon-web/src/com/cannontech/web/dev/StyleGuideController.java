package com.cannontech.web.dev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.Direction;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.sort.SortableData;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.collect.ImmutableList;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class StyleGuideController {
    
    Comparator<Population> cityCompare = new Comparator<Population>() {
        @Override
        public int compare(Population o1, Population o2) {
            return o1.getCity().compareTo(o2.getCity());
        }
    };
    Comparator<Population> popCompare = new Comparator<Population>() {
        @Override
        public int compare(Population o1, Population o2) {
            return Long.compare(o1.getPopulation(), o2.getPopulation());
        }
    };
    List<Comparator<Population>> compares = ImmutableList.of(cityCompare, popCompare);
    
    @RequestMapping({"/styleguide", "/styleguide/"})
    public String root() {
        return "redirect:styleguide/grids";
    }
    
    @RequestMapping("/styleguide/grids")
    public String grids() {
        return "styleguide/grids.jsp";
    }
    
    @RequestMapping("/styleguide/tables")
    public String tables(ModelMap model) {
        
        model.addAttribute("signup", new Signup());
        model.addAttribute("signupTypes", SignupType.values());
        
        List<Population> data = new ArrayList<>();
        data.add(new Population("Daluth", 86211));
        data.add(new Population("Minneapolis", 392880));
        data.add(new Population("St. Paul", 290770));
        
        SortableColumn c1 = new SortableColumn(Direction.desc, false, true, "City");
        SortableColumn c2 = new SortableColumn(Direction.desc, false, true, "Population");
        List<SortableColumn> columns = ImmutableList.of(c1, c2);
        
        SortableData pops = new SortableData(data, columns);
        model.addAttribute("pops", pops);
        
        return "styleguide/tables.jsp";
    }
    
    @RequestMapping("/styleguide/tables/sort-example")
    public String tables(ModelMap model, int sort, Direction dir) {
        
        List<Population> data = new ArrayList<>();
        data.add(new Population("Daluth", 86211));
        data.add(new Population("Minneapolis", 392880));
        data.add(new Population("St. Paul", 290770));
        
        Comparator<Population> comparator = compares.get(sort);
        
        if (dir == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        
        Collections.sort(data, comparator);
        
        SortableColumn c1 = new SortableColumn(dir, sort == 0 ? true : false, true, "City");
        SortableColumn c2 = new SortableColumn(dir, sort == 1 ? true : false, true, "Population");
        List<SortableColumn> columns = ImmutableList.of(c1, c2);
        
        SortableData pops = new SortableData(data, columns);
        model.addAttribute("pops", pops);
        
        return "styleguide/sort-example.jsp";
    }
    
    public class Population {
        private String city;
        private long population;
        public Population(String city, long population) {
            this.city = city;
            this.population = population;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        public long getPopulation() {
            return population;
        }
        public void setPopulation(long population) {
            this.population = population;
        }
    }
    
    @RequestMapping("/styleguide/containers")
    public String containers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/containers.jsp";
    }
    
    @RequestMapping("/styleguide/buttons")
    public String buttons(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        return "styleguide/buttons.jsp";
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
        Instant past = new Instant(now.minus(Duration.standardDays(5)).getMillis());
        Instant start = new Instant(now.minus(Duration.standardDays(7)).getMillis());
        Instant end = new Instant(now.plus(Duration.standardDays(7)).getMillis());
        model.addAttribute("now", now);
        model.addAttribute("past", past);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        
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
    
    public class Signup {
        
        private String name = "Bob Vila";
        private SignupType type;
        private boolean enabled = true;
        private String notes = "wow such name much value many gap very checkbox so textfield";
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public SignupType getType() {
            return type;
        }
        public void setType(SignupType type) {
            this.type = type;
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public String getNotes() {
            return notes;
        }
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
    }
    public enum SignupType {MONTHY, YEARLY} 
}