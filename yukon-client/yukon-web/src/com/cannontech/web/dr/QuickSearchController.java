package com.cannontech.web.dr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NotPaoTypeFilter;
import com.cannontech.dr.quicksearch.QuickSearchFilter;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.dr.service.DemandResponseService.CombinedSortableField;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.PaoDetailUrlHelper;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
/**
 * Controller for DR quick search
 */
public class QuickSearchController {
    @Autowired private FilterService filterService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private FavoritesDao favoritesDao;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private DemandResponseService demandResponseService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    public class QuickSearchRowMapper implements RowMapperWithBaseQuery<DisplayablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
        	Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_CONTROL_AREA,
        												PaoTag.LM_SCENARIO,
        												PaoTag.LM_PROGRAM, 
        												PaoTag.LM_GROUP);
            SqlStatementBuilder fragment = new SqlStatementBuilder();
            fragment.append("SELECT PAO.PAOName, PAO.PAObjectId, PAO.Type ");
            fragment.append("FROM YukonPAObject PAO");
            fragment.append("WHERE (PAO.Type").in(paoTypes).append(")");
            
            return fragment;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            return null;
        }

        @Override
        public boolean needsWhere() {
            return false;
        }

        @Override
        public DisplayablePao mapRow(YukonResultSet rs) throws SQLException {
            int paoId = rs.getInt("paObjectId");
            String paoType = rs.getString("type");
            PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.getForDbString(paoType));
            DisplayablePaoBase pao = new DisplayablePaoBase(paoIdentifier, rs.getString("paoName"));
            return pao;
        }
    }
    
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(ModelMap modelMap, YukonUserContext userContext, 
                         @ModelAttribute("quickSearchBean") ListBackingBean quickSearchBean) {
        LiteYukonUser user = userContext.getYukonUser();

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));
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
        boolean descending = quickSearchBean.getDescending();
        Comparator<DisplayablePao> sorter = null;
        if (sort != null) {
            CombinedSortableField sortField =
                CombinedSortableField.valueOf(sort);
            if (sortField != null) {
                sorter = demandResponseService.getSorter(sortField, userContext);
                if (descending && sorter != null) {
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
}
