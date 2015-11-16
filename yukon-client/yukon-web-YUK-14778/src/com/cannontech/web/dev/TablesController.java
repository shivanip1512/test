package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.dev.model.Population;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class TablesController {
    
    private static List<Population> cities;

    private Comparator<Population> cityCompare = new Comparator<Population>() {
        @Override
        public int compare(Population o1, Population o2) {
            return o1.getCity().compareTo(o2.getCity());
        }
    };
    private Comparator<Population> popCompare = new Comparator<Population>() {
        @Override
        public int compare(Population o1, Population o2) {
            return Long.compare(o1.getPopulation(), o2.getPopulation());
        }
    };
    private Map<String, Comparator<Population>> compares = ImmutableMap.of("city", cityCompare, "pop", popCompare);
    
    @RequestMapping("/styleguide/tables")
    public String tables(ModelMap model) {
        
        model.addAttribute("signup", new Signup());
        model.addAttribute("signupTypes", SignupType.values());
        
        SortableColumn c1 = SortableColumn.of(Direction.desc, false, "City", "city");
        SortableColumn c2 = SortableColumn.of(Direction.desc, false, "Population", "pop");
        List<SortableColumn> columns = ImmutableList.of(c1, c2);
        
        ArrayList<Population> copy = Lists.newArrayList(cities);
        Collections.shuffle(copy);
        SearchResults<Population> paged = SearchResults.pageBasedForWholeList(1, 10, copy);
        
        model.addAttribute("columns", columns);
        model.addAttribute("pops", paged);
        model.addAttribute("allPops", copy);
        
        return "styleguide/tables.jsp";
    }
    
    @RequestMapping("/styleguide/tables/sort-example")
    public String tables(ModelMap model, 
            @DefaultSort(dir=Direction.asc, sort="city") SortingParameters sorting, 
            @DefaultItemsPerPage(10) PagingParameters paging) {
        
        ArrayList<Population> copy = Lists.newArrayList(cities);
        
        // sort the list
        Comparator<Population> comparator = compares.get(sorting.getSort());
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(copy, comparator);
        
        // page the list
        SearchResults<Population> paged = SearchResults.pageBasedForWholeList(paging, copy);
        model.addAttribute("pops", paged);
        
        // add columns
        SortableColumn c1 = SortableColumn.of(sorting, "City", "city");
        SortableColumn c2 = SortableColumn.of(sorting, "Population", "pop");
        model.addAttribute("columns", ImmutableList.of(c1, c2));
        
        return "styleguide/sort-example.jsp";
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
    
    static {
        Builder<Population> b = ImmutableList.builder();
        b.add(new Population("Minneapolis", 392880));
        b.add(new Population("Saint Paul", 290770));
        b.add(new Population("Rochester", 108992));
        b.add(new Population("Duluth", 86211));
        b.add(new Population("Bloomington", 86033));
        b.add(new Population("Brooklyn Park", 77752));
        b.add(new Population("Plymouth", 72928));
        b.add(new Population("St. Cloud", 65986));
        b.add(new Population("Eagan", 64854));
        b.add(new Population("Woodbury", 64496));
        b.add(new Population("Maple Grove", 64420));
        b.add(new Population("Eden Prairie", 62258));
        b.add(new Population("Coon Rapids", 61931));
        b.add(new Population("Burnsville", 61130));
        b.add(new Population("Blaine", 59412));
        b.add(new Population("Lakeville", 57342));
        b.add(new Population("Minnetonka", 51123));
        b.add(new Population("Apple Valley", 49978));
        b.add(new Population("Edina", 49050));
        b.add(new Population("St. Louis Park", 46362));
        b.add(new Population("Mankato", 40119));
        b.add(new Population("Maplewood", 39337));
        b.add(new Population("Moorhead", 39039));
        b.add(new Population("Shakopee", 38744));
        b.add(new Population("Richfield", 36087));
        b.add(new Population("Cottage Grove", 35181));
        b.add(new Population("Roseville", 34666));
        b.add(new Population("Inver Grove Heights", 34198));
        b.add(new Population("Andover", 31200));
        b.add(new Population("Brooklyn Center", 30643));
        b.add(new Population("Savage", 27959));
        b.add(new Population("Winona", 27944));
        b.add(new Population("Oakdale", 27726));
        b.add(new Population("Fridley", 27639));
        b.add(new Population("Shoreview", 25628));
        b.add(new Population("Owatonna", 25421));
        b.add(new Population("Austin", 24800));
        b.add(new Population("White Bear Lake", 24311));
        b.add(new Population("Chaska", 24074));
        b.add(new Population("Ramsey", 24071));
        b.add(new Population("Chanhassen", 23840));
        b.add(new Population("Prior Lake", 23754));
        b.add(new Population("Champlin", 23694));
        b.add(new Population("Faribault", 23394));
        b.add(new Population("Elk River", 23273));
        b.add(new Population("Crystal", 22524));
        b.add(new Population("Rosemount", 22420));
        b.add(new Population("Hastings", 22321));
        b.add(new Population("Farmington", 21926));
        b.add(new Population("New Brighton", 21867));
        b.add(new Population("Golden Valley", 20776));
        b.add(new Population("Lino Lakes", 20746));
        b.add(new Population("New Hope", 20728));
        b.add(new Population("Northfield", 20515));
        b.add(new Population("South St. Paul", 20404));
        b.add(new Population("West St. Paul", 19708));
        b.add(new Population("Willmar", 19674));
        b.add(new Population("Columbia Heights", 19667));
        b.add(new Population("Forest Lake", 18957));
        b.add(new Population("Stillwater", 18542));
        b.add(new Population("Hopkins", 17982));
        b.add(new Population("Albert Lea", 17898));
        b.add(new Population("Anoka", 17243));
        b.add(new Population("St. Michael", 16765));
        b.add(new Population("Red Wing", 16481));
        b.add(new Population("Hibbing", 16287));
        b.add(new Population("Sartell", 16183));
        b.add(new Population("Buffalo", 15724));
        b.add(new Population("Ham Lake", 15552));
        b.add(new Population("Robbinsdale", 14263));
        b.add(new Population("Otsego", 14138));
        b.add(new Population("Hutchinson", 13929));
        b.add(new Population("Hugo", 13834));
        b.add(new Population("Bemidji", 13723));
        b.add(new Population("Brainerd", 13517));
        b.add(new Population("Marshall", 13446));
        b.add(new Population("North Mankato", 13369));
        b.add(new Population("New Ulm", 13265));
        b.add(new Population("Fergus Falls", 13215));
        b.add(new Population("Sauk Rapids", 12965));
        b.add(new Population("Monticello", 12964));
        b.add(new Population("Worthington", 12870));
        b.add(new Population("Vadnais Heights", 12764));
        b.add(new Population("Mounds View", 12407));
        b.add(new Population("Cloquet", 12036));
        b.add(new Population("North St. Paul", 11694));
        b.add(new Population("Rogers", 11641));
        b.add(new Population("East Bethel", 11596));
        b.add(new Population("Alexandria", 11549));
        b.add(new Population("St. Peter", 11427));
        b.add(new Population("Waconia", 11222));
        b.add(new Population("Mendota Heights", 11138));
        b.add(new Population("Grand Rapids", 10916));
        b.add(new Population("Fairmont", 10463));
        b.add(new Population("Big Lake", 10247));
        b.add(new Population("Little Canada", 10008));
        b.add(new Population("North Branch", 10006));
        b.add(new Population("Arden Hills", 9750));
        b.add(new Population("Hermantown", 9603));
        b.add(new Population("Waseca", 9434));
        b.add(new Population("Mound", 9238));
        b.add(new Population("Detroit Lakes", 8812));
        b.add(new Population("Thief River Falls", 8661));
        b.add(new Population("Virginia", 8645));
        b.add(new Population("East Grand Forks", 8548));
        b.add(new Population("St. Anthony", 8392));
        b.add(new Population("Little Falls", 8288));
        b.add(new Population("Cambridge", 8217));
        b.add(new Population("Oak Grove", 8112));
        b.add(new Population("Lake Elmo", 8106));
        b.add(new Population("Mahtomedi", 7883));
        b.add(new Population("Crookston", 7844));
        b.add(new Population("Victoria", 7805));
        b.add(new Population("Orono", 7720));
        b.add(new Population("Wyoming", 7716));
        b.add(new Population("Baxter", 7700));
        b.add(new Population("Shorewood", 7468));
        b.add(new Population("New Prague", 7428));
        b.add(new Population("St. Francis", 7312));
        b.add(new Population("Albertville", 7172));
        b.add(new Population("Belle Plaine", 6804));
        b.add(new Population("Minnetrista", 6681));
        b.add(new Population("Waite Park", 6680));
        b.add(new Population("Litchfield", 6671));
        b.add(new Population("St. Joseph", 6646));
        b.add(new Population("Spring Lake Park", 6448));
        b.add(new Population("International Falls", 6357));
        b.add(new Population("Kasson", 6006));
        b.add(new Population("Stewartville", 5991));
        b.add(new Population("Jordan", 5832));
        b.add(new Population("Glencoe", 5562));
        b.add(new Population("Delano", 5559));
        b.add(new Population("Corcoran", 5500));
        b.add(new Population("Falcon Heights", 5443));
        b.add(new Population("Isanti", 5395));
        b.add(new Population("St. Paul Park", 5324));
        b.add(new Population("Morris", 5277));
        b.add(new Population("Montevideo", 5247));
        b.add(new Population("Zimmerman", 5246));
        b.add(new Population("Redwood Falls", 5175));
        b.add(new Population("Medina", 5045));
        b.add(new Population("Chisholm", 5025));
        b.add(new Population("Lake City", 5005));
        b.add(new Population("Byron", 4995));
        b.add(new Population("Circle Pines", 4944));
        b.add(new Population("Chisago City", 4920));
        b.add(new Population("Dayton", 4833));
        b.add(new Population("La Crescent", 4816));
        b.add(new Population("Luverne", 4680));
        b.add(new Population("Princeton", 4674));
        b.add(new Population("Windom", 4606));
        b.add(new Population("Becker", 4605));
        b.add(new Population("St. James", 4587));
        b.add(new Population("North Oaks", 4575));
        b.add(new Population("Oak Park Heights", 4575));
        b.add(new Population("Nowthen", 4500));
        b.add(new Population("Elko New Market", 4415));
        b.add(new Population("Lindstrom", 4408));
        b.add(new Population("Rockford", 4370));
        b.add(new Population("Sauk Centre", 4322));
        b.add(new Population("Watertown", 4239));
        b.add(new Population("Pipestone", 4196));
        b.add(new Population("Grant", 4126));
        b.add(new Population("Dilworth", 4091));
        b.add(new Population("Wadena", 4090));
        b.add(new Population("Cannon Falls", 4081));
        b.add(new Population("Carver", 4037));
        b.add(new Population("Le Sueur", 4029));
        b.add(new Population("Cold Spring", 4027));
        b.add(new Population("Goodview", 4004));
        b.add(new Population("Scandia", 3984));
        b.add(new Population("Columbus", 3949));
        b.add(new Population("Centerville", 3838));
        b.add(new Population("Wayzata", 3777));
        b.add(new Population("Lonsdale", 3757));
        b.add(new Population("Deephaven", 3718));
        b.add(new Population("St. Charles", 3699));
        b.add(new Population("Eveleth", 3697));
        b.add(new Population("Two Harbors", 3692));
        b.add(new Population("Park Rapids", 3681));
        b.add(new Population("Independence", 3610));
        b.add(new Population("Bayport", 3605));
        b.add(new Population("Melrose", 3601));
        b.add(new Population("Norwood Young America", 3583));
        b.add(new Population("Sleepy Eye", 3524));
        b.add(new Population("Mora", 3507));
        b.add(new Population("Newport", 3453));
        b.add(new Population("Ely", 3438));
        b.add(new Population("St. Augusta", 3431));
        b.add(new Population("Long Prairie", 3409));
        b.add(new Population("Breckenridge", 3387));
        b.add(new Population("Jackson", 3307));
        b.add(new Population("Plainview", 3304));
        b.add(new Population("Annandale", 3298));
        b.add(new Population("Pine Island", 3297));
        b.add(new Population("Zumbrota", 3293));
        b.add(new Population("Blue Earth", 3283));
        b.add(new Population("Benson", 3164));
        b.add(new Population("Pine City", 3083));
        b.add(new Population("Rush City", 3065));
        b.add(new Population("Proctor", 3054));
        b.add(new Population("Perham", 3010));
        
        cities = b.build();
    }
    
}
