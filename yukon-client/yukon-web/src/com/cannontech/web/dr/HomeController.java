package com.cannontech.web.dr;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.favorites.service.FavoritesService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.dr.service.DemandResponseService.CombinedSortableField;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class HomeController {
    @Autowired private FavoritesDao favoritesDao;
    @Autowired private FavoritesService favoritesService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private DemandResponseService demandResponseService;

    @RequestMapping("/home")
    public String home(ModelMap model, YukonUserContext userContext,
            String favSort, Boolean favDescending, String rvSort,
            Boolean rvDescending) {
        LiteYukonUser user = userContext.getYukonUser();

        List<UiFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, user,
                                         Permission.LM_VISIBLE));

        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);

        List<DisplayablePao> favorites = favoritesService.getFavorites(user, filter);
        Comparator<DisplayablePao> sorter = null;
        if (favSort != null) {
            CombinedSortableField sortField = CombinedSortableField.valueOf(favSort);
            sorter = demandResponseService.getSorter(sortField, userContext);
            if (favDescending != null && favDescending && sorter != null) {
                sorter = Ordering.from(sorter).reverse();
            }
        }
        if (sorter == null) {
            sorter = new DisplayablePaoComparator();
        }
        Collections.sort(favorites, sorter);
        model.addAttribute("favorites", favorites);

        List<DisplayablePao> recentlyViewed = favoritesService.getRecentlyViewed(user, 20, filter);
        sorter = null;
        if (rvSort != null) {
            CombinedSortableField sortField = CombinedSortableField.valueOf(rvSort);
            sorter = demandResponseService.getSorter(sortField, userContext);
            if (rvDescending != null && rvDescending && sorter != null) {
                sorter = Ordering.from(sorter).reverse();
            }
        }
        if (sorter == null) {
            sorter = new DisplayablePaoComparator();
        }
        Collections.sort(recentlyViewed, sorter);
        model.addAttribute("recents", recentlyViewed);

        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(recentlyViewed,
                                        userContext.getYukonUser());
        model.addAttribute("favoritesByPaoId", favoritesByPaoId);

        return "dr/home.jsp";
    }

    @RequestMapping("/details")
    public String details(ModelMap model, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();
        
        boolean showControlAreas = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, user);
        boolean showScenarios = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, user);

        // The Details link defaults to control area list, if control areas are hidden,
        // goes to scenarios, if they are hidden goes to programs
        
        String link = "/spring/dr/controlArea/list";
        if(!showControlAreas) {
            if(showScenarios) {
                link = "/spring/dr/scenario/list"; 
            } else {
                link = "/spring/dr/program/list";
            }
        }

        return "redirect:" + link;
    }

    @RequestMapping("/addFavorite")
    public ModelAndView addFavorite(int paoId, YukonUserContext userContext)
            throws Exception {
        favoritesDao.addFavorite(paoId, userContext.getYukonUser());
        return favoriteUpdated();
    }

    @RequestMapping("/removeFavorite")
    public ModelAndView removeFavorite(int paoId, YukonUserContext userContext)
            throws Exception {
        favoritesDao.removeFavorite(paoId, userContext.getYukonUser());
        return favoriteUpdated();
    }

    private ModelAndView favoriteUpdated() {
        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("favoriteDidUpdate", true);
        return mav;
    }
}
