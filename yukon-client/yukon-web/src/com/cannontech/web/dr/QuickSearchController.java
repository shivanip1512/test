package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.filter.NotPaoTypeFilter;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.cannontech.dr.quicksearch.QuickSearchFilter;
import com.cannontech.dr.quicksearch.QuickSearchRowMapper;
import com.cannontech.user.YukonUserContext;

@Controller
/**
 * Controller for DR quick search
 */
public class QuickSearchController {
    private FilterService filterService;
    private RolePropertyDao rolePropertyDao;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(ModelMap modelMap, YukonUserContext userContext, 
                         @ModelAttribute("quickSearchBean") ListBackingBean quickSearchBean) {
        LiteYukonUser user = userContext.getYukonUser();

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        boolean showControlAreas = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, user);
        if (!showControlAreas) {
            filters.add(new NotPaoTypeFilter(PaoType.LM_CONTROL_AREA));
        }
        boolean showScenarios = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, user);
        if (!showScenarios) {
            filters.add(new NotPaoTypeFilter(PaoType.LM_SCENARIO));
        }

        String searchText = quickSearchBean.getName();
        filters.add(new QuickSearchFilter(searchText));

        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator(false);

        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (quickSearchBean.getPage() - 1) * quickSearchBean.getItemsPerPage();
        SearchResult<DisplayablePao> searchResult = 
                filterService.filter(filter, 
                                     sorter, 
                                     startIndex, 
                                     quickSearchBean.getItemsPerPage(), 
                                     new QuickSearchRowMapper());
            
        modelMap.addAttribute("searchResult", searchResult);

        return "dr/searchResults.jsp";
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
