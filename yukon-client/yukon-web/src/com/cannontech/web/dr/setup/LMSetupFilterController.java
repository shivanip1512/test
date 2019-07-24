package com.cannontech.web.dr.setup;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
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

@Controller
@RequestMapping("/setup")
public class LMSetupFilterController {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final Logger log = YukonLogManager.getLogger(LMSetupFilterController.class);
    
    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext) {
        LMSetupFilter lmSetupFilter = null;
        if (!model.containsAttribute("lmSetupFilter")) {
            lmSetupFilter = new LMSetupFilter();
            lmSetupFilter.setFilterByType(LmSetupFilterType.CONTROL_AREA);
        } else {
            lmSetupFilter = (LMSetupFilter) model.get("lmSetupFilter");
        }
        filter(lmSetupFilter, SortingParameters.of("NAME", Direction.desc), PagingParameters.EVERYTHING, model,
            userContext);
        return "dr/setup/list.jsp";
    }
    
    @GetMapping("/filter")
    public String filter(@ModelAttribute LMSetupFilter lmSetupFilter, @DefaultSort(dir = Direction.desc, sort = "NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        FilterCriteria<LMSetupFilter> filterCriteria =
                new FilterCriteria<LMSetupFilter>(lmSetupFilter, sorting, paging);
        
        //TODO add a call to api
        log.info("Filter By:" + lmSetupFilter.getFilterByType());
        log.info("Name:" + lmSetupFilter.getName());
        log.info("Types: ");
        List<PaoType> types = ((LMSetupFilter)filterCriteria.getFilteringParameter()).getTypes();
        CollectionUtils.emptyIfNull(types).stream().forEach(type -> log.info(type));
        log.info("Paging: " + filterCriteria.getPagingParameters());
        log.info("Sorting: " + filterCriteria.getSortingParameters());
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        if (lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_GROUP
            || lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_PROGRAM) {
            for (SortBy column : SortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                model.addAttribute(column.name(), col);
            }
        } else {
            String text = accessor.getMessage(SortBy.NAME);
            SortableColumn col = SortableColumn.of(dir, SortBy.NAME == sortBy, text, SortBy.NAME.name());
            model.addAttribute(SortBy.NAME.name(), col);
        }
        
        SearchResults<LMPaoDto> filteredResults = mockSearchResults(lmSetupFilter.getFilterByType());
        
        // Build setup model
        String viewUrlPrefix = null;
        switch (lmSetupFilter.getFilterByType()) {
        case CONTROL_AREA:
            viewUrlPrefix = "/dr/setup/controlArea/";
            break;
        case LOAD_GROUP:
            viewUrlPrefix = "/dr/setup/loadGroup/";
            break;
        case LOAD_PROGRAM:
            viewUrlPrefix = "/dr/setup/loadProgram/";
            break;
        case CONTROL_SCENARIO:
            viewUrlPrefix = "/dr/setup/controlScenario/";
            break;
        case MACRO_LOAD_GROUP:
            viewUrlPrefix = "/dr/setup/macroLoadGroup/";
            break;
        case PROGRAM_CONSTRAINT:
            viewUrlPrefix = "/dr/setup/contraint/";
            break;
        default:
            throw new RuntimeException("viewUrlPrefix is not set for " + lmSetupFilter.getFilterByType());
        }
        model.addAttribute("viewUrlPrefix", viewUrlPrefix);
        model.addAttribute("filteredResults", filteredResults);
        model.addAttribute("loadGroupTypes", PaoType.getAllLMGroupTypes());
        model.addAttribute("loadProgramTypes", PaoType.getAllLMProgramTypes());
        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        model.addAttribute("lmSetupFilter", lmSetupFilter);
        return "dr/setup/list.jsp";
    }
    
    private enum SortBy implements DisplayableEnum {

        NAME, TYPE;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();
        }

    }
    
    
    // TODO: remove this after adding api call.
    private SearchResults<LMPaoDto> mockSearchResults(LmSetupFilterType filterByType) {
        SearchResults<LMPaoDto> searchResults = new SearchResults<>();
        searchResults.setResultList(Lists.newArrayList());

        if (filterByType == LmSetupFilterType.LOAD_GROUP) {
            LMPaoDto pao1 = new LMPaoDto(123, "Load Group 1", PaoType.LM_GROUP_ECOBEE);
            LMPaoDto pao2 = new LMPaoDto(123, "Load Group 2", PaoType.LM_GROUP_HONEYWELL);
            searchResults.getResultList().add(pao1);
            searchResults.getResultList().add(pao2);
            searchResults.setBounds(0, 3, 1);
        } else if (filterByType == LmSetupFilterType.LOAD_PROGRAM) {
            LMPaoDto pao1 = new LMPaoDto(123, "Load Program 1", PaoType.LM_ECOBEE_PROGRAM);
            LMPaoDto pao2 = new LMPaoDto(123, "Load Program 2", PaoType.LM_HONEYWELL_PROGRAM);
            searchResults.getResultList().add(pao1);
            searchResults.getResultList().add(pao2);
            searchResults.setBounds(0, 3, 1);
        } else {
            for (int i = 0; i < 10; i++) {
                LMPaoDto dto = new LMPaoDto(i, "Search Result " + i, null);
                searchResults.getResultList().add(dto);
            }
            searchResults.setBounds(0, 10, 1);
        }

        return searchResults;
    }
}
