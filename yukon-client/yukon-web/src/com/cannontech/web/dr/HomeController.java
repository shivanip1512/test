package com.cannontech.web.dr;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.favorites.service.FavoritesService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.EmptyView;
import com.google.common.collect.Lists;

@Controller
public class HomeController {
    private FavoritesDao favoritesDao;
    private FavoritesService favoritesService;
    private RolePropertyDao rolePropertyDao;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/home")
    public String home(ModelMap model, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        List<UiFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(new AuthorizedFilter(paoAuthorizationService, user,
                                         Permission.LM_VISIBLE));

        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);

        List<DisplayablePao> favorites = favoritesService.getFavorites(user, filter);
        model.addAttribute("favorites", favorites);

        List<DisplayablePao> recentlyViewed = favoritesService.getRecentlyViewed(user, 20, filter);
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
    public View addFavorite(HttpServletResponse response, int paoId,
            YukonUserContext userContext) {
        favoritesDao.addFavorite(paoId, userContext.getYukonUser());
        return new EmptyView();
    }

    @RequestMapping("/removeFavorite")
    public View removeFavorite(HttpServletResponse response, int paoId,
            YukonUserContext userContext) {
        favoritesDao.removeFavorite(paoId, userContext.getYukonUser());
        return new EmptyView();
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }

    @Autowired
    public void setFavoritesService(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
}
