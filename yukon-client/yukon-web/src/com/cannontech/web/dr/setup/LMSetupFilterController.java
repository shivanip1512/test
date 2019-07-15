package com.cannontech.web.dr.setup;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/setup")
public class LMSetupFilterController {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final Logger log = YukonLogManager.getLogger(LMSetupFilterController.class);
    private static final String baseKey = "yukon.web.modules.dr.setup.";

    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext) {
        if(!model.containsAttribute("filterByType")) {
            filterLoadGroups(new LoadGroupBase(), SortingParameters.of("NAME", Direction.desc), PagingParameters.EVERYTHING, model, userContext);
        }
        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        return "dr/setup/list/list.jsp";
    }

    @GetMapping("/loadFilterByOptions/{filterByType}")
    public String loadFilterByOptions(@PathVariable LmSetupFilterType filterByType, ModelMap model) {
        model.addAttribute("filterByType", filterByType);
        switch (filterByType) {
        case LOAD_GROUP:
            model.addAttribute("switchTypes", PaoType.getAllLMGroupTypes());
            if (model.containsAttribute("loadGroupBase")) {
                model.addAttribute("loadGroupBase", (LoadGroupBase) model.get("loadGroupBase"));
            } else {
                model.addAttribute("loadGroupBase", new LoadGroupBase());
            }
        case LOAD_PROGRAM:
            if (model.containsAttribute("loadProgram")) {
                model.addAttribute("loadProgram", (LoadProgram) model.get("loadProgram"));
            } else {
                model.addAttribute("loadProgram", new LoadProgram());
            }
        }
        return "dr/setup/list/filterForm.jsp";
    }

    @RequestMapping(value = "/filterLoadGroups", method = RequestMethod.GET)
    public String filterLoadGroups(@ModelAttribute LoadGroupBase loadGroupBase,
            @DefaultSort(dir = Direction.desc, sort = "NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LoadGroupSortBy sortBy = LoadGroupSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (LoadGroupSortBy column : LoadGroupSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        FilterCriteria<LoadGroupBase> filterCriteria =
            new FilterCriteria<LoadGroupBase>(loadGroupBase, sorting, paging);
        log.info("Load Group Filter Parameters");
        log.info("Name: " + ((LoadGroupBase)filterCriteria.getFilteringParameter()).getName());
        log.info("Switch Type: " + ((LoadGroupBase)filterCriteria.getFilteringParameter()).getType());
        log.info("Paging: " + filterCriteria.getPagingParameters());
        log.info("Sorting: " + filterCriteria.getSortingParameters());
        
        // TODO: Replace mockSearchResults() with API call to filter Load Groups.
        SearchResults<LoadGroupBase> filteredResults = mockSearchResults();
        model.addAttribute("filteredResults", filteredResults);
        
        Map<Integer, String> loadGroupDetails = Maps.newHashMap();
        List<LoadGroupBase> loadGroups = filteredResults.getResultList();
        for (LoadGroupBase loadGroup : loadGroups) {
            StringBuilder detailsStr = new StringBuilder();
            if (loadGroup.getType() == PaoType.LM_GROUP_EXPRESSCOMM || loadGroup.getType() == PaoType.LM_GROUP_RFN_EXPRESSCOMM) {
                LoadGroupExpresscom  loadGroupExpresscom = (LoadGroupExpresscom)loadGroup;
                detailsStr.append(accessor.getMessage("yukon.common.route"));
                detailsStr.append(":" + loadGroupExpresscom.getRouteId() + ";");
                detailsStr.append(accessor.getMessage(baseKey + "loadGroup.serviceProvider") + ":");
                detailsStr.append(loadGroupExpresscom.getServiceProvider() + ";");
                detailsStr.append(accessor.getMessage(baseKey + "loadGroup.program") + ":");
                detailsStr.append(loadGroupExpresscom.getProgram() + ";");
                detailsStr.append(accessor.getMessage(baseKey + "loadGroup.splinter") + ":");
                detailsStr.append(loadGroupExpresscom.getSplinter() + ";");
                detailsStr.append(accessor.getMessage(baseKey + "loadGroup.controlPriority") + ":");
                detailsStr.append(loadGroupExpresscom.getProtocolPriority() + ";");
            } else {
                detailsStr.append(accessor.getMessage("yukon.common.na"));
            }
            loadGroupDetails.put(loadGroup.getId(), detailsStr.toString());
        }
        model.addAttribute("loadGroupDetails", loadGroupDetails);
        
        model.addAttribute("switchTypes", PaoType.getAllLMGroupTypes());
        
        model.addAttribute("filterByType", LmSetupFilterType.LOAD_GROUP);
        model.addAttribute("loadGroupBase", loadGroupBase);
        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        return "dr/setup/list/list.jsp";
    }

    //TODO: To be changed while working on the YUK for load programs.
    @RequestMapping(value = "/filterLoadPrograms", method = RequestMethod.GET)
    public String filterLoadPrograms(@ModelAttribute LoadProgram loadProgram, ModelMap model) {
        SearchResults<LoadProgram> filteredResults = new SearchResults<>();
        model.addAttribute("filteredResults", filteredResults);
        model.addAttribute("filterByType", LmSetupFilterType.LOAD_PROGRAM);
        model.addAttribute("loadGroupBase", loadProgram);
        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        return "dr/setup/list/list.jsp";
    }

    private SearchResults<LoadGroupBase> mockSearchResults() {
        SearchResults<LoadGroupBase> searchResults = new SearchResults<>();
        searchResults.setResultList(Lists.newArrayList());

        LoadGroupBase loadGroupBase = new LoadGroupBase();
        loadGroupBase.setId(124293);
        loadGroupBase.setName("Point Group Test");
        loadGroupBase.setType(PaoType.LM_GROUP_HONEYWELL);
        searchResults.getResultList().add(loadGroupBase);

        LoadGroupBase loadGroupBase1 = new LoadGroupBase();
        loadGroupBase1.setId(126216);
        loadGroupBase1.setName("SEP Group");
        loadGroupBase1.setType(PaoType.LM_GROUP_ECOBEE);
        searchResults.getResultList().add(loadGroupBase1);
        
        LoadGroupBase loadGroupBase2 = new LoadGroupBase();
        loadGroupBase2.setId(148086);
        loadGroupBase2.setName("A009");
        loadGroupBase2.setType(PaoType.LM_GROUP_ITRON);
        searchResults.getResultList().add(loadGroupBase2);
        
        
        searchResults.setBounds(0, 3, 1);

        return searchResults;
    }

    public enum LoadGroupSortBy implements DisplayableEnum {

        NAME, SWITCH_TYPE;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.loadGroup.filter." + name();
        }

    }

}
