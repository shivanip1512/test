package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.filter.NotPaoTypeFilter;
import com.cannontech.dr.quicksearch.QuickSearchFilter;
import com.cannontech.dr.quicksearch.QuickSearchRowMapper;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.dr.service.DemandResponseService.CombinedSortableField;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.PaoDetailUrlHelper;
import com.google.common.collect.Ordering;

@Controller
/**
 * Controller for DR quick search
 */
public class QuickSearchController {
    private FilterService filterService;
    private RolePropertyDao rolePropertyDao;
    private FavoritesDao favoritesDao;
    private PaoDetailUrlHelper paoDetailUrlHelper;
    private DemandResponseService demandResponseService;

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

        String sort = quickSearchBean.getSort();
        Boolean descending = quickSearchBean.getDescending();
        Comparator<DisplayablePao> sorter = null;
        if (sort != null) {
            CombinedSortableField sortField =
                CombinedSortableField.valueOf(sort);
            if (sortField != null) {
                sorter = demandResponseService.getSorter(sortField, userContext);
                if (descending != null && descending && sorter != null) {
                    sorter = Ordering.from(sorter).reverse();
                }
            }
        }
        if (sorter == null) {
            sorter = new DisplayablePaoComparator();
        }

        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (quickSearchBean.getPage() - 1) * quickSearchBean.getItemsPerPage();
        SearchResult<DisplayablePao> searchResult = 
                filterService.filter(filter, 
                                     sorter, 
                                     startIndex, 
                                     quickSearchBean.getItemsPerPage(), 
                                     new QuickSearchRowMapper());

        if (searchResult.getHitCount() == 1) {
            DisplayablePao pao = searchResult.getResultList().get(0);
            String detailUrl = paoDetailUrlHelper.getUrlForPaoDetailPage(pao);
            if (detailUrl != null) {
                return "redirect:" + detailUrl;
            }
        }

        modelMap.addAttribute("searchResult", searchResult);
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        modelMap.addAttribute("favoritesByPaoId", favoritesByPaoId);

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

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }

    @Autowired
    public void setPaoDetailUrlHelper(PaoDetailUrlHelper paoDetailUrlHelper) {
        this.paoDetailUrlHelper = paoDetailUrlHelper;
    }

    @Autowired
    public void setDemandResponseService(DemandResponseService demandResponseService) {
        this.demandResponseService = demandResponseService;
    }
}
