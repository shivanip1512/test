package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.sort.SortableData;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class TablesController {

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
    Map<String, Comparator<Population>> compares = ImmutableMap.of("city", cityCompare, "pop", popCompare);
    
    @RequestMapping("/styleguide/tables")
    public String tables(ModelMap model) {
        
        model.addAttribute("signup", new Signup());
        model.addAttribute("signupTypes", SignupType.values());
        
        List<Population> data = new ArrayList<>();
        data.add(new Population("Daluth", 86211));
        data.add(new Population("Minneapolis", 392880));
        data.add(new Population("St. Paul", 290770));
        
        SortableColumn c1 = new SortableColumn(Direction.desc, false, true, "City", "city");
        SortableColumn c2 = new SortableColumn(Direction.desc, false, true, "Population", "pop");
        List<SortableColumn> columns = ImmutableList.of(c1, c2);
        
        SortableData pops = new SortableData(data, columns);
        model.addAttribute("pops", pops);
        
        return "styleguide/tables.jsp";
    }
    
    @RequestMapping("/styleguide/tables/sort-example")
    public String tables(ModelMap model, SortingParameters sorting) {
        
        List<Population> data = new ArrayList<>();
        data.add(new Population("Daluth", 86211));
        data.add(new Population("Minneapolis", 392880));
        data.add(new Population("St. Paul", 290770));
        
        Comparator<Population> comparator = compares.get(sorting.getSort());
        
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        
        Collections.sort(data, comparator);
        
        boolean sortByCity = sorting.getSort().equalsIgnoreCase("city") ? true : false;
        boolean sortByPop = sorting.getSort().equalsIgnoreCase("pop") ? true : false;
        SortableColumn c1 = new SortableColumn(sorting.getDirection(), sortByCity, true, "City", "city");
        SortableColumn c2 = new SortableColumn(sorting.getDirection(), sortByPop, true, "Population", "pop");
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
    
    public enum SignupType { MONTHY, YEARLY }
    
}
